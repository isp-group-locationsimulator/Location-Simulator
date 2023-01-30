package com.ispgr5.locationsimulator

import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentConverter
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.presentation.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * This class handles the interaction with the local Storage to import and export Configurations
 */
class StorageConfigInterface(private val mainActivity: MainActivity) {

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
     * This function safes the given configuration into app local Download Folder(Android/data/com.ispgr5.locationsimulator/files/Download)
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun safeConfigurationToStorage(configuration: Configuration) {
        val downloadDir = mainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        //TODO handle configurations with the same name. Now there are stored the new Configuration in the same file in a new line
        val file = File(downloadDir, configuration.name + ".txt")
        val jsonString = JSONObject()
        jsonString.put("name", configuration.name)
        jsonString.put("description", configuration.description)
        jsonString.put(
            "configurationComponents",
            ConfigurationComponentConverter().componentListToString(configuration.components)
        )
        file.appendText(jsonString.toString())
    }

    /**
     * This variable lets us read a file that we choose and store the read Configuration into Database
     */
    @OptIn(DelicateCoroutinesApi::class, ExperimentalSerializationApi::class)
    private val readFile =
        //open file picker
        mainActivity.registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let { fileUri ->
                //read file
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
                    //TODO tell user there was a problem
                    println("fehler beim lesen")
                }
                val jsonObj = JSONObject(stringBuilder.toString())
                //Store to Database
                GlobalScope.launch {
                    configurationUseCases?.addConfiguration?.let {
                        it(
                            Configuration(
                                name = jsonObj.get("name") as String,
                                description = jsonObj.get("description") as String,
                                components = ConfigurationComponentConverter().componentStrToComponentList(
                                    jsonObj.get("configurationComponents") as String
                                )
                            )
                        )
                    }
                }
            }
        }
}