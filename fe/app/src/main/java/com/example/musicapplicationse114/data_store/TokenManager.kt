package com.example.musicapplicationse114.repositories

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val KEY_TOKEN = "access_token"

    // Lưu token
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    // Lấy token
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // Xóa token
    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }
}