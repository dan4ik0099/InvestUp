package com.example.investup.publicObject

object SortingObject {

    enum class PostsSort(val s: String) {
        CREATED_AT("createdAt"),
        VIEWS("views")
    }

    enum class SortValue(val s: String){
        DESC("desc"),
        ASC("asc")
    }


}