package com.example.investup.dataModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataModelUser: ViewModel() {
    val id: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val firstName: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val lastName: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val dialogId: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
}