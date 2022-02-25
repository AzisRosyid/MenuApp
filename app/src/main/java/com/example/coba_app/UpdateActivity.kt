package com.example.coba_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.bumptech.glide.Glide
import com.example.coba_app.databinding.ActivityUpdateBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UpdateActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {

    lateinit var binding: ActivityUpdateBinding
    private val api by lazy { ApiRetrofit().apiEndPoint }
    private val menu by lazy { intent.getSerializableExtra("menu") as Menu }
    private var selectedImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Update Menu"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.nameMenu.setText(menu.name)
        binding.priceMenu.setText(menu.price.toString())
        binding.carboMenu.setText(menu.carbo.toString())
        binding.proteinMenu.setText(menu.protein.toString())
        Glide.with(this)
            .load(Method.BASE_IMAGE + menu.photo)
            .centerCrop()
            .error(R.drawable.ic_baseline_image_24)
            .into(binding.imageMenu)
        setupListener()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setupListener(){
        binding.btnUpdate.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            if(binding.nameMenu.text.isEmpty() || binding.priceMenu.text.isEmpty() || binding.carboMenu.text.isEmpty() || binding.proteinMenu.text.isEmpty()) {
                Method.message("All field must be filled", this, false)
            }
            else if (!binding.priceMenu.text.isDigitsOnly() || !binding.carboMenu.text.isDigitsOnly() || !binding.proteinMenu.text.isDigitsOnly()) {
                Method.message("Price, Carbo, and Protein format must be number",this, false)
            }
            else if (selectedImage == null){
                Method.message("Select image first", this, false)
            }
            else {
                uploadImage()
            }
            binding.progressBar.visibility = View.GONE
        }
        binding.uploadImage.setOnClickListener {
            openImageChooser()
        }
    }

    private fun uploadImage() {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.progressImage.visibility = View.VISIBLE
        binding.progressImage.progress = 0
        val body = UploadRequestBody(file, "image", this)
        api.updateMenu(
            menu.id,
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), binding.nameMenu.text.toString()),
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), binding.priceMenu.text.toString()),
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), binding.carboMenu.text.toString()),
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), binding.proteinMenu.text.toString()),
            MultipartBody.Part.createFormData("photo", file.name, body)
        ).enqueue(object :
            Callback<ResponseModel> {
            override fun onResponse(
                call: Call<ResponseModel>,
                response: Response<ResponseModel>
            ) {
                if(!response.isSuccessful){
                    val errors = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(baseContext, errors.getString("errors"), Toast.LENGTH_SHORT).show()
                } else {
                    Method.message(response.body()!!.message, this@UpdateActivity, true)
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Log.e("onFailure", t.toString())
            }
        })
        binding.progressImage.progress = 100
        binding.progressImage.visibility = View.GONE
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_IMAGE_PICKER ->{
                    selectedImage = data?.data
                    binding.imageMenu.setImageURI(selectedImage)
                }
            }
        }
    }

    override fun onProgressUpdate(percentage: Int) {

    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}