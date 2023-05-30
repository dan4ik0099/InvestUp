package com.example.investup.retrofit.requestModel

data class PostEditRequest(


val title: String?,
val shortDescription: String?,
val description: String?,

val tags: ArrayList<String>

)
