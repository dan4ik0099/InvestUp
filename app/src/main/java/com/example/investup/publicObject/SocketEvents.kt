package com.example.investup.publicObject

enum class SocketEvents(val s: String) {
    CONNECTION("connection"),
    MESSAGE("message"),
    CREATE_DIALOG("createDialog"),
    ERROR("error"),
    NOTIFY("notify"),
    READ_MESSAGE("readMessage"),
    DELETE_MESSAGE("deleteMessage")
}