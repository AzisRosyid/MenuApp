package com.example.coba_app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.view.View
import com.example.coba_app.cart.Cart
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

object Method {
    var id: Int = 0
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var level: String = ""
    var total: Double = 0.0

    val BASE_IMAGE = "http://172.16.8.56/MyLaravel/Coba_Laravel/public/images/"

    fun message(message: String, activity: Activity, action: Boolean){
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle("Message")
            .setMessage(message)
            .setPositiveButton("Ok"){dialog, which ->
                if(action){
                    activity.finish()
                }
            }
        alertDialog.show()
    }

    var cart = ArrayList<Cart>()
}

fun View.snackbar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar -> snackbar.setAction("Ok") { snackbar.dismiss() } }
}

@SuppressLint("Range")
fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return name
}

class UploadRequestBody(
    private val file: File,
    private val contentType: String,
    private val callback: UploadCallback
) : RequestBody() {
    interface UploadCallback {
        fun onProgressUpdate(percentage: Int)
    }

    inner class ProgressUpdate(
        private val uploaded: Long,
        private val total: Long
    ): Runnable {
        override fun run() {
            callback.onProgressUpdate((100 * uploaded / total).toInt())
        }

    }

    override fun contentType() = "$contentType/*".toMediaTypeOrNull()

    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())

            while(fileInputStream.read(buffer).also{ read = it } != -1) {
                handler.post(ProgressUpdate(uploaded, length))
                uploaded += read
                sink.write(buffer, 0 , read)
                sink.write(buffer, 0, read)
            }
        }
    }

    companion object{
        private const val DEFAULT_BUFFER_SIZE = 1048
    }

}
