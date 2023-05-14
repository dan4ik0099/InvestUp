package com.example.investup.navigationInterface

import androidx.fragment.app.Fragment


fun Fragment.navigator(): Navigator{
    return requireActivity() as Navigator
}

interface Navigator {
    fun navToUserProfile()
    fun navToEditPost()
    fun navToPostDetails()
    fun navAfterLoginRegister()
    fun navToFavorite()
    fun goToRegister()
    fun goToLogin()
    fun navOn()
    fun navToHome()
    fun navToChat()
    fun navToProfile()
    fun navToEditProfile()
    fun navToAddPost()

}