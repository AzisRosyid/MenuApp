package com.example.coba_app

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coba_app.cart.Cart
import com.example.coba_app.databinding.AdapterCartBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CartAdapter(val cart: ArrayList<Cart>, val listener: onSetupListener): RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    class ViewHolder(val binding: AdapterCartBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cart = cart[position]
        holder.binding.nameMenu.setText(cart.name)
        holder.binding.priceMenu.setText(NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(cart.price))
        holder.binding.qtyMenu.setText(cart.qty.toString())
        Glide.with(holder.itemView)
            .load(Method.BASE_IMAGE + cart.photo)
            .centerCrop()
            .error(R.drawable.ic_baseline_restaurant_menu_24)
            .into(holder.binding.imgMenu)

        holder.binding.qtyMenu.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(!holder.binding.qtyMenu.text.isNullOrEmpty()){
                    cart.qty = holder.binding.qtyMenu.text.toString().toInt()
                    listener.onTextChanged(cart)
                }
            }
        })
        holder.binding.btnDelete.setOnClickListener {
            listener.onDelete(cart)
        }
    }

    override fun getItemCount(): Int {
        return cart.size
    }

    fun setData(data: List<Cart>){
        cart.clear()
        cart.addAll(data)
        notifyDataSetChanged()
    }

    interface onSetupListener{
        fun onTextChanged(cart: Cart)
        fun onDelete(cart: Cart)
    }
}