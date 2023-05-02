package com.example.investup.retrofit.requestModel

data class PostAddRequest(


    val title: String?,
    val description: String?,
    val shortDescription: String?,
    val video: String?,
    val tags: ArrayList<String>?

)
