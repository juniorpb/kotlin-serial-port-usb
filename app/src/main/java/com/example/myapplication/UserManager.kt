package com.example.myapplication

object UserManager {
    private var UserName: String? = null

    fun setUserName(name: String) {
        UserName = name
    }

    fun getUserName(): String? {
        return UserName
    }
}