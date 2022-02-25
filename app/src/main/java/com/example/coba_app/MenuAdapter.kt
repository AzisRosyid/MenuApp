package com.example.coba_app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coba_app.databinding.AdapterMenuBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MenuAdapter(val menu: ArrayList<Menu>, val listener: onSetupListener): RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    class ViewHolder(val binding: AdapterMenuBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menu[position]
        holder.binding.nameMenu.setText(menu.name)
        holder.binding.priceMenu.setText(NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(menu.price))
        holder.binding.carboMenu.setText(menu.carbo.toString())
        holder.binding.proteinMenu.setText(menu.protein.toString())
        Glide.with(holder.itemView)
            .load(Method.BASE_IMAGE + menu.photo)
            .centerCrop()
            .error(R.drawable.ic_baseline_restaurant_menu_24)
            .into(holder.binding.imgMenu)

        holder.binding.btnUpdate.setOnClickListener{
            listener.onUpdate(menu)
        }
        holder.binding.btnDelete.setOnClickListener {
            listener.onDelete(menu)
        }
    }

    override fun getItemCount(): Int {
        return menu.size
    }

    fun setData(data: List<Menu>){
        menu.clear()
        menu.addAll(data)
        notifyDataSetChanged()
    }

    interface onSetupListener{
        fun onUpdate(menu: Menu)
        fun onDelete(menu: Menu)
    }

}