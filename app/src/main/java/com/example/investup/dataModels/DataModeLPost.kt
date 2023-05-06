package com.example.investup.dataModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataModeLPost : ViewModel(){

    val id: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

}