package com.example.coba_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.example.coba_app.databinding.ActivityLoginBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private val api by lazy { ApiRetrofit().apiEndPoint }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Login Menu"
        setupListener()
    }

    private fun setupListener(){
        binding.btnLogin.setOnClickListener{
            if(binding.emailUser.text.isNullOrEmpty() || binding.passwordUser.text.isNullOrEmpty()){
                Method.message("All field must be filled", this, false)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailUser.text).matches()){
                Method.message("Email format must be email", this, false)
            } else {
                api.login(
                    binding.emailUser.text.toString(),
                    binding.passwordUser.text.toString()
                ).enqueue(object: Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if(!response.isSuccessful){
                            val errors = JSONObject(response.errorBody()!!.string())
                            Method.message(errors.getString("errors"), this@LoginActivity, false)
                        } else {
                            Method.id = response.body()!!.users.id
                            Method.name = response.body()!!.users.name
                            Method.email = response.body()!!.users.email
                            Method.password = binding.passwordUser.text.toString()
                            Method.level = response.body()!!.users.level
                            if(Method.level == "admin"){
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }else if (Method.level == "user"){
                                startActivity(Intent(applicationContext, UserActivity::class.java))
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Log.e("onFailure", t.message.toString())
                    }
                })
            }
        }
    }
}