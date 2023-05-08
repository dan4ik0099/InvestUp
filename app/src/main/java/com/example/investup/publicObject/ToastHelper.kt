package com.example.investup.publicObject

import android.app.Activity
import android.content.res.Resources
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.investup.R

object ToastHelper {
    fun toast(activity: Activity, successStringId: Int, unSuccessStringId: Int, message: String) {

        Toast.makeText(
            activity,
            activity.resources.getText(if (message == "OK") successStringId else unSuccessStringId ),
            Toast.LENGTH_SHORT
        ).show()


    }
}