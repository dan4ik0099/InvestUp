package com.example.investup.dataModels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataModelSearch {
    class DataModelAddPost : ViewModel(){

        val searchHome: MutableLiveData<String> by lazy{
            MutableLiveData<String>()
        }
        val tagHome: MutableLiveData<ArrayList<String>> by lazy{
            MutableLiveData<ArrayList<String>>()
        }
        val searchFavorite: MutableLiveData<String> by lazy{
            MutableLiveData<String>()
        }
        val tagFavorite: MutableLiveData<ArrayList<String>> by lazy{
            MutableLiveData<ArrayList<String>>()
        }
        val searchProfile: MutableLiveData<String> by lazy{
            MutableLiveData<String>()
        }
        val tagProfile: MutableLiveData<ArrayList<String>> by lazy{
            MutableLiveData<ArrayList<String>>()
        }

    }
}