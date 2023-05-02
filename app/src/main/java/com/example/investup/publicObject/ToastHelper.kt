package com.example.investup.publicObject

import android.app.Activity
import android.content.res.Resources
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.investup.R

object ToastHelper {
    fun toast(activity: Activity ,id: Int){
        Toast.makeText(
            activity,
            activity.resources.getText(id),
            Toast.LENGTH_SHORT
        ).show()
    }
}