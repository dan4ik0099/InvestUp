package com.example.investup.dataModels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.investup.retrofit.dataClass.Tag


class DataModelSearch : ViewModel() {

    val searchHome: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val tagHome: MutableLiveData<ArrayList<Tag>> by lazy {
        MutableLiveData<ArrayList<Tag>>()
    }
    val sortHome: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val searchFavorite: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val tagFavorite: MutableLiveData<ArrayList<Tag>> by lazy {
        MutableLiveData<ArrayList<Tag>>()
    }
    val sortFavorite: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }


    val searchProfile: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val tagProfile: MutableLiveData<ArrayList<Tag>> by lazy {
        MutableLiveData<ArrayList<Tag>>()
    }
    val sortProfile: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val searchUserProfile: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val tagUserProfile: MutableLiveData<ArrayList<Tag>> by lazy {
        MutableLiveData<ArrayList<Tag>>()
    }
    val sortUserProfile: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

}
