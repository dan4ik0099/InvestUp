package com.example.investup.retrofit.dataClass

data class Post(
    val id: String,
    val updateAt: String,
    val createdAt: String,
    val title: String,
    val description: String,
    val shortDescription: String,
    val status: String,
    val videoUrl: String,
    val tags: ArrayList<Tag>,
    val user: User,


    )


