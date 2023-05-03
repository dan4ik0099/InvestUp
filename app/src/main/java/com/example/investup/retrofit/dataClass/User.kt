package com.example.investup.retrofit.dataClass

data class User(


    val email: String,
    val roles: ArrayList<Role>,
    val createdAt: String,
    val updatedAt: String,


    val id: String,
    val isConfirmedEmail: Boolean,
    val firstName: String,
    val lastName: String,
    val avatar: String


) {

}
