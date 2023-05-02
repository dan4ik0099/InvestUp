package com.example.investup.dataModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataModelToken : ViewModel() {
    val accessToken: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val refreshToken: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
}