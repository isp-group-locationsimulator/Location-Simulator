package com.ispgr5.locationsimulator.data.storageManager

import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.ispgr5.locationsimulator.domain.model.*
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.presentation.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class handles the interaction with the local Storage to import and export Configurations
 */
class ConfigurationStorageManager(
    private val mainActivity: MainActivity,
    private val soundStorageManager: SoundStorageManager
) {
    /**
     * This class helps to import and export Sound Files and maps the Sound name to his base64String
     */
    data class SoundHelp(val name: String, val base64String: String)

    /***********************************************\
     *                                              |
     * This Section is for Export Configurations    |
     *                                              |
     **********************************************/

    /**
     * This function safes the given configuration into app local Download Folder(Android/data/com.ispgr5.locationsimulator/files/Download)
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun safeConfigurationToStorage(configuration: Configuration) {
        //this is the app local Download Folder(Android/data/com.ispgr5.locationsimulator/files/Download)
        val downloadDir = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

        //read current time and date and safe it with the filename(so there are no duplicates)
        val dateTimeString = SimpleDateFormat(
            "dd.MM.yyyy_HH:mm:ss",
            Locale.getDefault()
        ).format(Date(System.currentTimeMillis()))

        val file = File(downloadDir, configuration.name + "_" + dateTimeString + ".txt")
        //when the user spams the Export Button and this function is called with in 1 second so the unique timestamp don't work
        if (file.exists()) {
            file.delete()
        }

        //add the Configuration to the jsonString
        val jsonString = JSONObject()
        jsonString.put("name", configuration.name)
        jsonString.put("description", configuration.description)
        jsonString.put(
            "configurationComponents",
            ConfigurationComponentConverter().componentListToString(configuration.components)
        )

        //add the Configuration Sound Files as Base64String from the ByteArrays of the File
        val soundList = mutableListOf<SoundHelp>()
        for (confComp in configuration.components) {
            when (confComp) {
                is Sound -> {
                    //look up if this sound is already in the soundList
                    var alreadyExist = false
                    for (sounds in soundList) {
                        if (sounds.name == confComp.source) {
                            alreadyExist = true
                            break
                        }
                    }
                    //safe the sound name and the Base64 String to the soundList
                    if (!alreadyExist) {
                        val byteArray = File(mainActivity.filesDir, confComp.source).readBytes()
                        val audioAsBase64String =
                            SoundConverter().encodeByteArrayToBase64String(byteArray)
                        soundList.add(SoundHelp(confComp.source, audioAsBase64String))
                    }
                }
            }
        }
        //add the soundList to the Json String
        jsonString.put(
            "sounds", Gson().toJson(soundList.toTypedArray(), Array<SoundHelp>::class.java)
        )

        //write the Json String into the File
        file.appendText(jsonString.toString())
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
    fun pickFileAndSafeToDatabase(configurationUseCases: ConfigurationUseCases) {
        this.configurationUseCases = configurationUseCases
        readFile.launch("text/*")
    }

    /**
     * This variable lets us read a file that we choose and store the read Configuration into Database
     */
    @OptIn(DelicateCoroutinesApi::class, ExperimentalSerializationApi::class)
    private val readFile =
        //open file picker
        mainActivity.registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { fileUri ->

                val fileContent: String?
                try {
                    fileContent = readFileFromUri(fileUri)
                } catch (exception: Exception) {
                    //TODO tell user there was a problem by reading the file
                    return@let
                }

                val jsonObj = JSONObject(fileContent)

                //read the soundList(Sounds that are needed for this configuration)
                val soundListJsonString: String = jsonObj.get("sounds") as String
                val soundList = Gson().fromJson(soundListJsonString, Array<SoundHelp>::class.java)

                //read the configuration components(Sounds and Vibrations)
                var components: List<ConfigComponent> =
                    ConfigurationComponentConverter().componentStrToComponentList(
                        jsonObj.get("configurationComponents") as String
                    )

                //Edit the sound names in configuration components List, when the Sound already exist
                components = editComponentList(components, soundList)

                //Store to Database
                GlobalScope.launch {
                    configurationUseCases?.addConfiguration?.let {
                        it(
                            Configuration(
                                name = jsonObj.get("name") as String,
                                description = jsonObj.get("description") as String,
                                components = components
                            )
                        )
                    }
                }
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
        compList: List<ConfigComponent>,
        soundList: Array<SoundHelp>
    ): List<ConfigComponent> {
        val compListMutable = compList.toMutableList()
        //look up all sounds in the new configuration
        for (i in compList.indices) {
            if (compList[i] is Sound) {
                val soundComp = compList[i] as Sound
                //extract this sound name and find it in the sound list
                val soundNameWithEnding: String = soundComp.source
                val soundHelpObject: SoundHelp? =
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
                        FileOutputStream(soundStorageManager.getFileInPrivateDir(soundNameWithEnding))
                    outputStream.write(
                        soundHelpObject?.let {
                            SoundConverter().decodeBase64StringToByteArray(
                                it.base64String
                            )
                        }
                    )
                    outputStream.close()
                } else { //they are the same because the Base64 Strings match
                    //don't safe the Sound but rename the reference in the SoundObject

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
    private fun readFileFromUri(fileUri: Uri): String {
        val stringBuilder = StringBuilder()
        try {
            mainActivity.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }
        } catch (exception: Exception) {
            throw Exception("Error by reading from uri: $fileUri")
        }
        return stringBuilder.toString()
    }

}