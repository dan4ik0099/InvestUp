package com.example.investup.retrofit.dataClass

data class Message(
    val id: String,
    var updatedAt: String,
    var createdAt: String,
    val dialogId: String,
    var text: String,
    val read: Boolean = true,
    val user: User
)
