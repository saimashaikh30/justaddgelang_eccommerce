package com.example.justaddgelang

import android.content.Context

object SharedPreferencesHelper {
    private const val PREF_NAME = "user_preferences"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_PHONE = "phone_number"
    private const val KEY_EMAIL = "email"
    private const val KEY_USERTYPE = "usertype"


    // Save user login status and details
    fun saveUserData(
        context: Context,
        isLoggedIn: Boolean,
        userId: String,
        username: String,
        password: String,
        phoneNumber: String,
        email: String,
        usertype: String // Add this line
    ) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            putString(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putString(KEY_PHONE, phoneNumber)
            putString(KEY_EMAIL, email)
            putString(KEY_USERTYPE, usertype) // Add this line
            apply()
        }
    }


    // Save just the login status (used for quick login state check)
    fun saveLoginStatus(context: Context, isLoggedIn: Boolean) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            apply()
        }
    }

    // Get login status
    fun isLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Get user ID
    fun getUserId(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_USER_ID, "N/A")
    }

    // Get username
    fun getUsername(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_USERNAME, "Guest")
    }

    // Get password (only if necessary, avoid storing plain-text passwords for security)
    fun getPassword(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_PASSWORD, "")
    }

    // Get phone number
    fun getPhoneNumber(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_PHONE, "N/A")
    }

    // Get email
    fun getEmail(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_EMAIL, "N/A")
    }

    fun getUsertype(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_USERTYPE, "user")
    }

    // Clear user data (for logout)
    fun clearUserData(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()  // Removes all saved data
            apply()
        }
    }
}
