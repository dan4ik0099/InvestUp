package com.example.investup.publicObject

import java.util.Stack

object ConstNavigation {
    var titleStack: Stack<String> = Stack()
    var currentFragmentStack: Stack<Int> = Stack()
    const val HOME = 1
    const val CHAT = 2
    const val LOGIN = 3
    const val REGISTER = 4
    const val FAVORITE = 5
    const val PROFILE = 6
    const val EDIT_PROFILE = 7
    const val ADD_POST = 8
    const val POST_DETAILS = 9
    const val EDIT_POST = 10
}