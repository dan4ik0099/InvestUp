package com.example.investup.publicObject

import android.app.Activity
import android.content.res.Resources
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.investup.R

object ToastHelper {
    fun toast(activity: Activity, successString: String, unSuccessString: String, message: String) {
        var res  = ""
        try {
            res = unSuccessString.substring(
                unSuccessString.indexOf(":") + 2,
                unSuccessString.lastIndexOf("\"")
            )
        }
        catch (gg: java.lang.Exception) {

        }
        Toast.makeText(
            activity,
            if (message == "OK") successString else res ,
            Toast.LENGTH_SHORT
        ).show()


    }
}