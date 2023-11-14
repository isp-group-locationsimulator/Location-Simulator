package com.ispgr5.locationsimulator.data.storageManager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.MutableState
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.github.slugify.Slugify
import com.ispgr5.locationsimulator.BuildConfig
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.SoundConverter
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.vdurmont.semver4j.Semver
import com.vdurmont.semver4j.SemverException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.joda.time.Instant
import org.joda.time.format.DateTimeFormatterBuilder
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

private const val TAG = "ConfStorageManager"
private const val EXPORT_INTERNAL_PATH = "exports"
private const val AUTHORITY_STORAGE_PROVIDER = "com.ispgr5.locationsimulator.fileprovider"
private const val MEDIA_TYPE_EXPORT = "application/json+gzip"
private val MEDIA_TYPE_IMPORT = listOf("application/x-gzip", "application/gzip")
private const val OUTPUT_TOKEN = "locsim"
private const val LIMIT_CONF_NAME_FOR_EXPORT = 12

private object JsonTokens {
    const val appId = "appId"
    const val appVersion = "appVersion"
}

@Serializable
data class ConfigurationSerializer(
    val name: String,
    val description: String? = null,
    val randomOrderPlayback: Boolean,
    val configurationComponents: List<ConfigComponent>,
    val sounds: List<SoundHelp>,
    val appId: String,
    val appVersion: String
) {

    /**
     * This class helps to import and export Sound Files and maps the Sound name to his base64String
     */
    @Serializable
    data class SoundHelp(val name: String, val base64String: String)

}

/**
 * This class handles the interaction with the local Storage to import and export Configurations
 */
class ConfigurationStorageManager(
    private val mainActivity: MainActivity,
    private val soundStorageManager: SoundStorageManager,
    private val context: Context,
    val snackbarContent: MutableState<SnackbarContent?>
) {

    private val prettyJson by lazy {
        Json {
            prettyPrint = true
        }
    }

    /***********************************************\
     *                                              |
     * This Section is for Export Configurations    |
     *                                              |
     **********************************************/

    /**
     * replace special characters in the configuration name (limiting to ASCII), and limit it to n chars, so the filename doesn't get too long
     */
    private fun slugifyConfigurationName(
        configuration: Configuration,
        limit: Int = LIMIT_CONF_NAME_FOR_EXPORT
    ) =
        when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Slugify uses the Optional class, added only in API 24
            true -> Slugify.builder().build().slugify(configuration.name).trim()
            else -> {
                // do it ourselves, except not quite as nicely
                val legalCharRegex = Regex("[0-9A-Za-z ]")
                configuration.name.mapNotNull { c: Char ->
                    when (legalCharRegex.matches(c.toString())) {
                        false -> null
                        else -> if (c == ' ') '-' else c
                    }
                }.joinToString("")
                // remove all characters that are not ASCII
            }
        }.take(limit).trim().trimEnd('-', '_')


    /**
     * This function shares the configuration using the system sharing actions, so the user
     * can select the target app. This approach avoids needing any kind of storage permission
     */
    fun exportConfigurationUsingShareSheet(context: Context, configuration: Configuration) {
        //read current time and date and save it with the filename (so there are no duplicates)
        val dtFormatter = DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmm").toFormatter()
        val dateTimeString = dtFormatter.print(Instant.now())
        val outputDir = context.filesDir.resolve(EXPORT_INTERNAL_PATH).also {
            it.mkdirs()
        }
        val outputSlug = slugifyConfigurationName(configuration)
        val outputFile =
            outputDir.resolve("${OUTPUT_TOKEN}_${(outputSlug)}_$dateTimeString.json.gz").also {
                it.createNewFile()
            }
        outputFile.outputStream().use { fileStream ->
            // we use a GZIP compression for our files, so that they don't get quite as large when multiple
            // sounds are included in the package
            // gzip compression saves about 200 KiB for a profile with four different sounds (521 vs 722 KiB)
            GZIPOutputStream(fileStream).use { gzip ->
                gzip.bufferedWriter().use { bufferedWriter ->
                    bufferedWriter.write(getConfigString(configuration))
                }
            }
        }

        val contentUri = FileProvider.getUriForFile(context, AUTHORITY_STORAGE_PROVIDER, outputFile)
        Log.d(TAG, "Sharing file using content uri: $contentUri")
        val shareIntent = ShareCompat.IntentBuilder(context).setStream(contentUri)
            .setType(MEDIA_TYPE_EXPORT).intent.apply {
                action = Intent.ACTION_SEND
                setDataAndType(contentUri, MEDIA_TYPE_EXPORT)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        context.startActivity(
            Intent.createChooser(
                shareIntent, context.getString(R.string.share_export_using)
            )
        )
    }

    private fun getConfigString(configuration: Configuration): String {
        val serializer = ConfigurationSerializer(
            appId = BuildConfig.APPLICATION_ID,
            appVersion = BuildConfig.VERSION_NAME,
            sounds = serializeSounds(configuration),
            name = configuration.name,
            description = configuration.description,
            randomOrderPlayback = configuration.randomOrderPlayback,
            configurationComponents = configuration.components,
        )
        return prettyJson.encodeToString(serializer)
    }

    private fun serializeSounds(configuration: Configuration): List<ConfigurationSerializer.SoundHelp> {
        val mappedNames = mutableListOf<String>()
        return configuration.components.mapNotNull { confComp ->
            when (confComp) {
                is ConfigComponent.Sound -> {
                    //look up if this sound is already in the soundList
                    if (mappedNames.contains(confComp.source)) {
                        return@mapNotNull null
                    }
                    //safe the sound name and the Base64 String to the soundList
                    val byteArray =
                        File(mainActivity.filesDir, "/Sounds/" + confComp.source).readBytes()
                    val audioAsBase64String =
                        SoundConverter().encodeByteArrayToBase64String(byteArray)
                    return@mapNotNull ConfigurationSerializer.SoundHelp(
                        confComp.source, audioAsBase64String
                    )
                }

                else -> null
            }
        }
    }

    /***********************************************\
     *                                             *
     * This Section is for Import Configurations   *
     *                                             *
    \**********************************************/

    /**
     * The Interface to the Database is stored here after receiving it with "pickFileAndSafeToDatabase" as parameter
     */
    private var configurationUseCases: ConfigurationUseCases? = null

    /**
     * This function opens a file picker and reads the file and store the inner configuration to Database
     */
    fun pickFileAndSafeToDatabase(
        configurationUseCases: ConfigurationUseCases,
    ) {
        this.configurationUseCases = configurationUseCases
        fileReader.launch(MEDIA_TYPE_IMPORT.toTypedArray())
    }

    private fun loadErrorCallback(exception: LoadException) {
        val formattedMessage = exception.formatMessage(context)
        Log.e(TAG, formattedMessage)
        snackbarContent.value =
            SnackbarContent(text = formattedMessage, snackbarDuration = SnackbarDuration.Long)
    }

    private fun loadSuccessCallback(configurationName: String) {
        snackbarContent.value =
            SnackbarContent(
                text = context.getString(R.string.success_reading_configuration_name)
                    .format(configurationName),
                snackbarDuration = SnackbarDuration.Short
            )
    }


    private suspend fun suspendingReadFileFromContentUri(uri: Uri, useCallback: Boolean): String? {
        try {
            val fileContent = readFileStringFromContentUri(contentUri = uri)
            verifyAppVersion(fileContent)
            val deserialized = Json.decodeFromString<ConfigurationSerializer>(fileContent)
            //Edit the sound names in configuration components List, when the Sound already exist
            val components =
                editComponentList(deserialized.configurationComponents, deserialized.sounds)
            configurationUseCases?.addConfiguration?.let { addConfiguration ->
                addConfiguration(
                    Configuration(
                        name = deserialized.name,
                        description = deserialized.description ?: "",
                        randomOrderPlayback = deserialized.randomOrderPlayback,
                        components = components
                    )
                )
            }
            if (useCallback) {
                loadSuccessCallback(deserialized.name)
            }
            return deserialized.name
        } catch (exception: Exception) {
            val exceptionForCallback = when (exception) {
                is SerializationException, is JSONException -> LoadException(R.string.json_load_exception)
                is LoadException -> exception
                else -> LoadException(
                    R.string.unknown_error_loading_exceptionname,
                    Exception::class.simpleName!!
                )
            }
            if (useCallback) {
                loadErrorCallback(exceptionForCallback)
            } else {
                throw exceptionForCallback
            }
        }
        return null
    }

    private fun verifyAppVersion(fileContent: String): Semver.VersionDiff? {
        val jsonObject = JSONObject(fileContent)
        when {
            !jsonObject.has(JsonTokens.appId) -> throw LoadException(
                R.string.configuration_doesnt_have_element,
                JsonTokens.appId
            )

            !jsonObject.has(JsonTokens.appVersion) -> throw LoadException(
                R.string.configuration_doesnt_have_element,
                JsonTokens.appVersion
            )

            jsonObject.getString(JsonTokens.appId) != BuildConfig.APPLICATION_ID -> throw LoadException(
                R.string.configuration_app_id_mismatch,
                JsonTokens.appId, BuildConfig.APPLICATION_ID, jsonObject.getString(JsonTokens.appId)
            )
        }
        val serializedVersion = jsonObject.getString(JsonTokens.appVersion)
        try {
            val serializedSemVer = Semver(serializedVersion)
            val appSemVer = Semver(BuildConfig.VERSION_NAME)
            val diff = serializedSemVer.diff(appSemVer)
            when (diff) {
                Semver.VersionDiff.MAJOR -> {
                    //TODO apply version migrations here
                    throw LoadException(
                        R.string.migration_version_not_supported,
                        serializedVersion,
                        appSemVer.toString()
                    )
                }

                Semver.VersionDiff.NONE -> {
                    Log.i(TAG, "No version difference")
                }

                else -> Log.i(
                    TAG,
                    "Serialized version '$serializedSemVer' vs. App version '$appSemVer' (diff: $diff)"
                )
            }
            return diff
        } catch (e: SemverException) {
            Log.e(TAG, "Exception thrown during verification of SemVer $serializedVersion", e)
            throw LoadException(R.string.could_not_verify_version, serializedVersion)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadConfigFromUriWithGlobalScope(uri: Uri) {
        GlobalScope.launch {
            suspendingReadFileFromContentUri(uri, true)
        }
    }

    /**
     * The value returned by this function lets us read a file that we choose and store the read Configuration into Database
     */
    private val fileReader =
        //open file picker
        mainActivity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { result: Uri? ->
            result?.let { fileUri ->
                loadConfigFromUriWithGlobalScope(fileUri)
            }
        }

    /**
     * This function looks up the already existing Sound files in the private dir and
     * their Base64 String. When the Sound already exists in fact of the same Base64 String then
     * the reference in Sound.source is renamed else this function safes the Sound file into the
     * private dir
     * @return the component list with renamed sound references if sound already exists
     */
    private fun editComponentList(
        compList: List<ConfigComponent>, soundList: List<ConfigurationSerializer.SoundHelp>
    ): List<ConfigComponent> {
        val compListMutable = compList.toMutableList()
        //look up all sounds in the new configuration
        for (i in compList.indices) {
            if (compList[i] is ConfigComponent.Sound) {
                val soundComp = compList[i] as ConfigComponent.Sound
                //extract this sound name and find it in the sound list
                val soundNameWithEnding: String = soundComp.source
                val soundHelpObject: ConfigurationSerializer.SoundHelp? =
                    soundList.find { soundHelp -> soundHelp.name == soundNameWithEnding }
                if (soundHelpObject == null) {
                    //TODO tell user that the imported Configuration don't have the right Sounds(soundNameWithEnding)
                    println("the imported Configuration don't have the right Sounds($soundNameWithEnding))")
                }

                //search this Sound in the already existing Sound files
                val soundAlreadyExistHere: String? =
                    soundHelpObject?.let { soundStorageManager.soundAlreadyExist(it.base64String) }

                //if this sound don't exist then write it to the private Folder
                if (soundAlreadyExistHere == null) {
                    val outputStream =
                        FileOutputStream(soundStorageManager.getFileInSoundsDir(soundNameWithEnding))
                    outputStream.write(soundHelpObject?.let {
                        SoundConverter().decodeBase64StringToByteArray(
                            it.base64String
                        )
                    })
                    outputStream.close()
                } else { //they are the same because the Base64 Strings match
                    //don't save the Sound but rename the reference in the SoundObject
                    compListMutable[i] = soundComp.myCopy(source = soundAlreadyExistHere)
                }
            }
        }
        return compListMutable.toList()
    }

    /**
     * This function reads the File Content from a file behind the given uri
     * @return a uri form a file u want to read
     */
    @Throws(LoadException::class)
    private fun readFileStringFromContentUri(contentUri: Uri): String {
        try {
            val loadFromRawJson: (InputStream) -> String = { ins ->
                ins.bufferedReader().readText()
            }
            val loadFromGzip: (InputStream) -> String = { ins ->
                GZIPInputStream(ins).let(loadFromRawJson)

            }
            val loader = when (val contentType = mainActivity.contentResolver.getType(contentUri)) {
                "application/x-gzip", "application/gzip" -> loadFromGzip
                null -> throw LoadException(
                    R.string.invalid_file_provided,
                    contentUri.toString()
                )
                else -> throw LoadException(
                    R.string.unsupported_media_type_provided,
                    contentType
                )
            }

            mainActivity.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                return loader(inputStream)
            }
        } catch (exception: Exception) {
            throw LoadException(
                R.string.unknown_error_loading_exceptionname, exception::class.simpleName!!
            )
        }
        throw LoadException(R.string.unknown_error)
    }

    suspend fun handleImportFromIntent(
        intent: Intent,
        configurationUseCases: ConfigurationUseCases
    ): String? {
        try {
            if (this.configurationUseCases == null) {
                this.configurationUseCases = configurationUseCases
            }
            when (intent.type) {
                in MEDIA_TYPE_IMPORT -> {
                    Log.i(TAG, "handleImportFromIntent: ${intent.clipData}")
                    val extraUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri?
                    }
                    val dataUri = intent.data
                    val clipDataUri = when (intent.clipData?.itemCount) {
                        1 -> intent.clipData?.getItemAt(0)?.uri
                        else -> null
                    }
                    val theUri = listOfNotNull(extraUri, dataUri, clipDataUri).firstOrNull()
                    Log.i(TAG, "Received ${intent.action} intent with media type '${intent.type}' and URI $theUri")
                    if (theUri != null) {
                        val newConfName =
                            suspendingReadFileFromContentUri(uri = theUri, useCallback = false)
                        Log.i(TAG, "loaded conf '$newConfName' from intent")
                        return newConfName
                    } else {
                        throw LoadException(R.string.null_uri)
                    }
                }

                null -> throw LoadException(R.string.unsupported_media_type_provided, "null")
                else -> throw LoadException(R.string.unsupported_media_type_provided, intent.type!!)
            }
        } catch (e: LoadException) {
            loadErrorCallback(e)
        }
        return null
    }
}

class LoadException(@StringRes val messageStringRes: Int, vararg formatArgs: String) :
    Exception() {
    val formatMessage: (Context) -> String = { context ->
        context.getString(messageStringRes).format(*formatArgs)
    }
}