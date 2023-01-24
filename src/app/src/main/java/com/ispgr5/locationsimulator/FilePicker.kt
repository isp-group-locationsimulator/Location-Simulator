package com.ispgr5.locationsimulator

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import com.ispgr5.locationsimulator.presentation.MainActivity
import java.io.File
import java.io.FileOutputStream

/**
 * This class uses the ActivityResultContracts to access the files we need.
 * TODO: Put the class into a better fitting dir?
 * @param mainActivity This class needs context to access the ActivityResultContracts and the file system.
 */
class FilePicker(mainActivity: MainActivity) {
    /**
     * This variable points to the private dir that we use to save our sound files.
     * I can't seem to access files dir with mainActivity.filesDir directly where needed.
     * If that is possible, this variable can be removed.
     */
    private val filesDir: File = mainActivity.filesDir

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


    // TODO: To be implemented
//    fun getUriOfFile() {
//        //moveFileToFolder.launch(arrayOf("audio/*"))
//    }


    /**
     * This function returns a File in our private dir with the name it is given.
     * TODO: Maybe we should test for duplicate file names here and resolve them if necessary.
     * @param fileName The name the file should have
     * @return The File
     */
    private fun getFileInPrivateDir(fileName: String): File {
        return File(filesDir, fileName)
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