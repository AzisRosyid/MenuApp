package com.example.coba_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import com.example.coba_app.databinding.ActivityMainBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val api by lazy { ApiRetrofit().apiEndPoint }
    lateinit var menuAdapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Menu List"
        setupList()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        getMenu()
    }

    private fun setupList(){
        menuAdapter = MenuAdapter(arrayListOf(), object : MenuAdapter.onSetupListener{
            override fun onUpdate(menu: Menu) {
                startActivity(Intent(applicationContext, UpdateActivity::class.java).putExtra("menu", menu))
            }

            override fun onDelete(menu: Menu) {
                alertDelete(menu)
            }
        })
        binding.recyclerView.apply {
            adapter = menuAdapter
        }
    }

    private fun setupListener(){
        binding.btnCreate.setOnClickListener {
            startActivity(Intent(applicationContext, CreateActivity::class.java))
        }
    }

    private fun alertDelete(menu: Menu){
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Are you sure delete this data?")
            .setTitle("Konfirmation?")
            .setPositiveButton("Yes"){dialog, which->
                binding.progressBar.visibility = View.VISIBLE
                api.deleteMenu(menu.id).enqueue(object: Callback<ResponseModel>{
                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        if(!response.isSuccessful){
                            val errors = JSONObject(response.errorBody()!!.string())
                            Method.message(errors.getString("errors"), this@MainActivity, false)
                        } else {
                            Method.message(response.body()!!.message, this@MainActivity, false)
                            onStart()
                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        Log.e("onFailure", t.message.toString())
                    }
                })
                binding.progressBar.visibility = View.GONE
            }
            .setNegativeButton("No"){dialog, which-> onStart()}
        alertDialog.show()
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
                            menuAdapter.setData(response.body()!!.menus)
                        }
                    }

                    override fun onFailure(call: Call<MenuModel>, t: Throwable) {
                        Log.e("onFailure", t.message.toString())
                    }
                })
                binding.progressBar.visibility = View.GONE
                return false
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
        val cartItem = menu!!.findItem(R.id.cart)
        cartItem.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMenu(){
        binding.progressBar.visibility = View.VISIBLE
        api.getMenu().enqueue(object: Callback<MenuModel> {
            override fun onResponse(call: Call<MenuModel>, response: Response<MenuModel>) {
                if(response.isSuccessful){
                    menuAdapter.setData(response.body()!!.menus)
                }
            }

            override fun onFailure(call: Call<MenuModel>, t: Throwable) {
                Log.e("onFailure", t.message.toString())
            }
        })
        binding.progressBar.visibility = View.GONE
    }
}