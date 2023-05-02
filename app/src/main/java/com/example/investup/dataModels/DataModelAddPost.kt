package com.example.investup.dataModels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataModelAddPost :ViewModel(){

        val title: MutableLiveData<String> by lazy{
            MutableLiveData<String>()
        }
        val shortDescription: MutableLiveData<String> by lazy{
            MutableLiveData<String>()
        }
        val fullDescription: MutableLiveData<String> by lazy{
            MutableLiveData<String>()
        }
        val tags: MutableLiveData<ArrayList<String>> by lazy{
            MutableLiveData<ArrayList<String>>()
        }
        val video: MutableLiveData<Uri> by lazy{
            MutableLiveData<Uri>()
        }

}