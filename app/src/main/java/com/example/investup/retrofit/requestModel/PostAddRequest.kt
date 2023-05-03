package com.example.investup.retrofit.requestModel

import okhttp3.MultipartBody

data class PostAddRequest(


    val title: String?,
    val description: String?,
    val shortDescription: String?,
    val video: MultipartBody.Part?,
    val tags: ArrayList<String>?

)
