package com.example.coba_app

import java.io.Serializable

data class MenuModel(
    val menus: List<Menu>,
    val total: Int
)

data class Menu(
    val carbo: Int,
    val created_at: String,
    val id: Int,
    val name: String,
    val photo: String,
    val price: Int,
    val protein: Int,
    val updated_at: String
): Serializable