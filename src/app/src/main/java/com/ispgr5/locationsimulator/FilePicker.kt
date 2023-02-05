package com.ispgr5.locationsimulator

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import com.ispgr5.locationsimulator.domain.model.SoundConverter
import com.ispgr5.locationsimulator.presentation.MainActivity
import java.io.*
import java.net.URI

/**
 * This class uses the ActivityResultContracts to access the files we need.
 * TODO: Put the class into a better fitting dir?
 * @param mainActivity This class needs context to access the ActivityResultContracts and the file system.
 */
class FilePicker(private val mainActivity: MainActivity) {

    /**
     * This variable lets us select a file and get its Uri.
     * At the moment redundant, to be used later (maybe)
     */
    private val moveFileToFolder = mainActivity.registerForActivityResult(
        ActivityResultContracts.OpenDocument()) {
        println(it.toString())
    }

    /**
     * This variable lets us copy a file that we choose to our private dir.
     */
    private val copyFile = mainActivity.registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
        result?.let { fileUri ->
            val inputStream = mainActivity.contentResolver.openInputStream(fileUri)
            val outputStream = FileOutputStream(getFileNameFromUri(mainActivity, fileUri)?.let {
                getFileInPrivateDir(
                    it
                )
            })
            inputStream?.copyTo(outputStream)
            outputStream.flush()
            outputStream.close()
        }
    }

    /**
     * This function launches the ActivityResultsLauncher that lets us select a audio file
     * that then will be moved into the private dir.
     */
    fun moveFileToPrivateFolder() {
        copyFile.launch("audio/*")
    }

    /**
     * This function creates a new .mp3 file with the byteArray in it. Simply a Sound File
     * @return the Uri of the created file
     */
    fun moveSoundByteArrayToPrivateFolder(byteArray: ByteArray): URI {
        val file = File(mainActivity.filesDir, "tempSoundFilexxXXX333XXXxxxtempSoundFile.mp3")
        if (file.exists()) {
            file.delete()
        }
        val outputStream = FileOutputStream(file)
        outputStream.write(byteArray)
        outputStream.close()
        return file.toURI()
    }


    // TODO: To be implemented
//    fun getUriOfFile() {
//        //moveFileToFolder.launch(arrayOf("audio/*"))
//    }

    /**
     * this function looks up all Sound files in our private dir and compare its Base64 String
     * @return the filename of the file with the matching base64 String. null when no matching file exist
     */
    fun soundAlreadyExist(soundsBase64String: String): String? {
        for (existingSoundName in getSoundFileNames()) {
            //get the Base64 String from the file that already exists
            val existingSoundAsBase64: String =
                SoundConverter().encodeByteArrayToBase64String(
                    File(
                        mainActivity.filesDir,
                        existingSoundName
                    ).readBytes()
                )
            if (existingSoundAsBase64 == soundsBase64String) {
                return existingSoundName
            }
        }
        return null
    }


    /**
     * This function returns a File in our private dir with the name it is given.
     * In case of a naming conflict, the new file gets a "_new" attached before the file extension.
     * @param fileName The name the file should have
     * @return The File
     */
    fun getFileInPrivateDir(fileName: String): File {
        val dir = File(mainActivity.filesDir, "")
        val files = dir.listFiles()
        return if (files == null) {
            File(mainActivity.filesDir, fileName)
        } else {
            val fileNames = files.map { it.name }
            var fileNameReturnValue = fileName
            var areThereCopies = true
            while (areThereCopies) {
                if (fileNames.contains(fileNameReturnValue)) {
                    // This should put a _new before the file-extension of a filename
                    fileNameReturnValue = fileNameReturnValue.substring(0, fileNameReturnValue.lastIndexOf(".")) +
                            "_new" + fileNameReturnValue.substring(fileNameReturnValue.lastIndexOf("."))
                } else {
                    areThereCopies = false
                }
            }
            File(mainActivity.filesDir, fileNameReturnValue)
        }
    }


    /**
     * This function gets the names of the Sound Files from the private dir.
     * @return The List of Strings that hold the Filenames (incl. File Extension, exl. path) to the Sound Files in the private dir.
     */
    fun getSoundFileNames(): List<String> {
        val dir = File(mainActivity.filesDir, "")
        val files = dir.listFiles()
        val names = mutableListOf<String>()
        if (files != null) {
            for (i in files.indices) {
                names += files[i].path.substringAfter(mainActivity.filesDir.toString() + "/")
            }
        }
        return names
    }


    /**
     * This function was taken from: https://stackoverflow.com/a/70795638
     * TODO: Remove the SuppressLint
     */
    @SuppressLint("Range")
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val fileName: String?
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }
}