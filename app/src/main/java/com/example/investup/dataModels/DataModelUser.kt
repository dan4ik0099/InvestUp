package com.example.investup.dataModels

import androidx.lifecycle.MutableLiveData

class DataModelUser {
    val firstName: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val lastName: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
}