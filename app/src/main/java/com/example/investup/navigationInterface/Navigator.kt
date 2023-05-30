package com.example.investup.navigationInterface

import androidx.fragment.app.Fragment


fun Fragment.navigator(): Navigator{
    return requireActivity() as Navigator
}

interface Navigator {
    fun navToUserProfile()
    fun navToEditPost()
    fun navToPostDetails()
    fun navBack()
    fun navAfterLoginRegister()
    fun navToFavorite()
    fun goToRegister()
    fun goToLogin()
    fun navOn()
    fun navToDialogUser()
    fun navToHome()
    fun navToChat()
    fun navToProfile()
    fun navToEditProfile()
    fun navToAddPost()

}