package com.example.investup.retrofit.dataClass

data class Comment(

    val id:String,
    val text:String,
    val createdAt:String,
    val updatedAt:String,
    val user: User,

)
