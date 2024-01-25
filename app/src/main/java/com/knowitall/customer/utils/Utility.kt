package com.knowitall.customer.utils

import android.content.Context
import java.io.*

object Utility {

    fun read(context: Context, fileName: String?): String? {
        return try {
            val fis: FileInputStream = context.openFileInput(fileName)
            val isr = InputStreamReader(fis)
            val bufferedReader = BufferedReader(isr)
            return bufferedReader.use { it.readText() }
        } catch (fileNotFound: FileNotFoundException) {
            null
        } catch (ioException: IOException) {
            null
        }
    }

    fun create(
        context: Context,
        fileName: String?,
        jsonString: String?
    ): Boolean {
        return try {
            val fos: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            if (jsonString != null) {
                fos.write(jsonString.toByteArray())
            }
            fos.close()
            true
        } catch (fileNotFound: FileNotFoundException) {
            false
        } catch (ioException: IOException) {
            false
        }
    }

    fun isFilePresent(context: Context, fileName: String): Boolean {
        val path: String =
            context.getFilesDir().getAbsolutePath().toString() + "/" + fileName
        val file = File(path)
        return file.exists()
    }
}