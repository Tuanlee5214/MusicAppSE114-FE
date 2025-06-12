package com.example.musicapplicationse114.ui.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplicationse114.common.enum.LoadStatus
import com.example.musicapplicationse114.model.UserLoginRequest
import com.example.musicapplicationse114.repositories.Api
import com.example.musicapplicationse114.repositories.MainLog
import com.example.musicapplicationse114.repositories.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val successMessage: String = "",
    var isShowPassword: Boolean = false,
    val status: LoadStatus = LoadStatus.Init()
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mainLog: MainLog?,
    private val api: Api?,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updateSuccessMessage(successMessage: String) {
        _uiState.value = _uiState.value.copy(successMessage = successMessage)
    }

    fun reset() {
        _uiState.value = _uiState.value.copy(status = LoadStatus.Init())
    }

    fun changeIsShowPassword() {
        _uiState.value = _uiState.value.copy(isShowPassword = !_uiState.value.isShowPassword)
    }

    fun login() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
                val result = api?.login(UserLoginRequest(_uiState.value.username, _uiState.value.password))
                if (result != null && result.isSuccessful) {
                    val accessToken = result.body()?.access_token
                    if (accessToken != null) {
                        _uiState.value = _uiState.value.copy(status = LoadStatus.Success())
                        updateSuccessMessage(result.body()?.message.toString())
                        tokenManager.saveToken(accessToken) // Lưu token
                    } else {
                        _uiState.value = _uiState.value.copy(
                            status = LoadStatus.Error(result.body()?.message.toString())
                        )
                        Log.e("SignUpError", "Response body: ${result.body()?.toString()}")
                        Log.e("SignUpError", "Response code: ${result.code()}")
                        Log.e("SignUpError", "AccessToken: ${result.body()?.access_token}")
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        status = LoadStatus.Error("Login failed: ${result?.message()}")
                    )
                }
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(status = LoadStatus.Error(ex.message.toString()))
                Log.e("LoginError", "Exception: ${ex.message}")
            }
        }
    }

    fun getUserName(): String {
        return _uiState.value.username
    }

    fun getToken(): String? {
        return tokenManager.getToken()
    }

    fun clearToken() {
        tokenManager.clearToken()
    }
}