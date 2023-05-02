package com.example.investup.dataModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataModelRegisterFragment : ViewModel() {
    val login: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val name: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val surname: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val accessToken: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val refreshToken: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }





}