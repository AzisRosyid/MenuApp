package com.example.coba_app.cart

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Cart(
    val menu_id: Int,
    val name: String,
    val price: Int,
    val carbo: Int,
    val protein: Int,
    var qty: Int,
    val photo: String,
    val user_id: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): Serializable
