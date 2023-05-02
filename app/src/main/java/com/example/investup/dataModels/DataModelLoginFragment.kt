package com.example.investup.dataModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataModelLoginFragment : ViewModel() {
    val login: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val password: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val accessToken: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val refreshToken: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }



}