package com.example.coba_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.SearchEvent
import android.view.View
import android.widget.SearchView
import com.example.coba_app.databinding.ActivityUserBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserActivity : AppCompatActivity() {

    lateinit var binding: ActivityUserBinding
    private val api by lazy { ApiRetrofit().apiEndPoint }
    lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "User Menu"
        setupList()
    }

    override fun onStart() {
        super.onStart()
        getMenu()
    }

    private fun setupList(){
        userAdapter = UserAdapter(arrayListOf(), object: UserAdapter.onSetupListener{
            override fun onClick(menu: Menu) {
                startActivity(Intent(applicationContext, DetailActivity::class.java).putExtra("menu", menu))
            }
        })
        binding.recyclerView.apply{
            adapter = userAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
            R.id.cart -> {
                startActivity(Intent(applicationContext, CartActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem = menu!!.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.progressBar.visibility = View.VISIBLE
                api.searchMenu(p0.toString()).enqueue(object: Callback<MenuModel>{
                    override fun onResponse(call: Call<MenuModel>, response: Response<MenuModel>) {
                        if(response.isSuccessful){
                            userAdapter.setData(response.body()!!.menus)
                        }
                    }

                    override fun onFailure(call: Call<MenuModel>, t: Throwable) {
                        Log.e("onFailure", t.message.toString())
                    }
                })
                binding.progressBar.visibility = View.GONE
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
        searchView.setOnCloseListener(object: SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                onStart()
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun getMenu(){
        binding.progressBar.visibility = View.VISIBLE
        api.getMenu().enqueue(object: Callback<MenuModel> {
            override fun onResponse(call: Call<MenuModel>, response: Response<MenuModel>) {
                if(response.isSuccessful){
                    userAdapter.setData(response.body()!!.menus)
                }
            }

            override fun onFailure(call: Call<MenuModel>, t: Throwable) {
                Log.e("onFailure", t.message.toString())
            }
        })
        binding.progressBar.visibility = View.GONE
    }
}