package com.example.investup.retrofit.dataClass

data class Dialog(
    val id: String,
    val user: User,
    val lastMessage: Message,
    var unreadableMessages: Int
)
