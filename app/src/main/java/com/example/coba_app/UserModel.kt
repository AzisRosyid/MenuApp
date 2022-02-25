package com.example.coba_app

data class UserModel(
    val users: Users
)

data class Users(
    val created_at: String,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val level: String,
    val name: String,
    val updated_at: String
)
