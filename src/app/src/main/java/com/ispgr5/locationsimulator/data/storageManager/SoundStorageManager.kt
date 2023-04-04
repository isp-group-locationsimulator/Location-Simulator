package com.ispgr5.locationsimulator.data.storageManager

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
 * @param mainActivity This class needs context to access the ActivityResultContracts and the file system.
 */
class SoundStorageManager(private val mainActivity: MainActivity) {

	/**
	 * This variable lets us copy a file that we choose to our sounds dir.
	 */
	private val copyFile =
		mainActivity.registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
			result?.let { fileUri ->
				val inputStream = mainActivity.contentResolver.openInputStream(fileUri)
				val outputStream = FileOutputStream(getFileNameFromUri(mainActivity, fileUri)?.let {
					getFileInSoundsDir(
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
	 * that then will be moved into the sounds dir.
	 */
	fun moveFileToSoundsFolder() {
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
	 * this function looks up all Sound files in our sounds dir and compare its Base64 String
	 * @return the filename of the file with the matching base64 String. null when no matching file exist
	 */
	fun soundAlreadyExist(soundsBase64String: String): String? {
		for (existingSoundName in getSoundFileNames()) {
			//get the Base64 String from the file that already exists
			val existingSoundAsBase64: String =
				SoundConverter().encodeByteArrayToBase64String(
					File(
						mainActivity.filesDir,
						"Sounds/$existingSoundName"
					).readBytes()
				)
			if (existingSoundAsBase64 == soundsBase64String) {
				return existingSoundName
			}
		}
		return null
	}


	/**
	 * This function returns a File in our sounds dir with the name it is given.
	 * In case of a naming conflict, the new file gets a "_new" attached before the file extension.
	 * @param fileName The name the file should have
	 * @return The File
	 */
	fun getFileInSoundsDir(fileName: String): File {
		val dir = File(mainActivity.filesDir, "Sounds")
		val files = dir.listFiles()
		return if (files == null) {
			File(mainActivity.filesDir, "Sounds/$fileName")
		} else {
			val fileNames = files.map { it.name }
			var fileNameReturnValue = fileName
			var areThereCopies = true
			while (areThereCopies) {
				if (fileNames.contains(fileNameReturnValue)) {
					// This should put a _new before the file-extension of a filename
					fileNameReturnValue = try {
						fileNameReturnValue.substring(0, fileNameReturnValue.lastIndexOf(".")) +
								"_new" + fileNameReturnValue.substring(
							fileNameReturnValue.lastIndexOf(
								"."
							)
						)
						// exception, when the parameter fileName doesn't have a file extension
					} catch (e: Exception) {
						fileNameReturnValue + "_new"
					}
				} else {
					areThereCopies = false
				}
			}
			File(mainActivity.filesDir, "Sounds/$fileNameReturnValue")
		}
	}


	/**
	 * This function gets the names of the Sound Files from the sound dir.
	 * @return The List of Strings that hold the Filenames (incl. File Extension, exl. path) to the Sound Files in the sounds dir.
	 */
	fun getSoundFileNames(): List<String> {
		val dir = File(mainActivity.filesDir, "Sounds")
		val files = dir.listFiles()
		val names = mutableListOf<String>()
		if (files != null) {
			for (i in files.indices) {
				names += files[i].path.substringAfter(mainActivity.filesDir.toString() + "/Sounds/")
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

	/**
	 * This function deletes a file from the sounds dir.
	 * @param fileNameToDelete The file that should be deleted.
	 */
	fun deleteFileFromSoundsDir(fileNameToDelete: String) {
		val pathToFile = File(mainActivity.filesDir.toString() + "/Sounds/" + fileNameToDelete)
		if (pathToFile.isFile) {
			pathToFile.delete()
		}
	}

	/**
	 * This function adds a sound file to a newly created sounds directory inside the privateDir.
	 * @param fileName The name the file should have.
	 * @param inputStream The InputStream of the file.
	 */
	fun addSoundFile(fileName: String, inputStream: InputStream) {
		val soundDir = File(mainActivity.filesDir, "Sounds")
		if (!soundDir.exists()) {
			soundDir.mkdir()
		}
		val file = File(soundDir, fileName)
		val outputStream = FileOutputStream(file)
		val bytes = inputStream.readBytes() // This is not recommended, see https://www.baeldung.com/kotlin/inputstream-to-file . Should be changed.
		outputStream.write(bytes)
	}
}