package com.paw.ecocycle.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paw.ecocycle.model.MainRepository
import com.paw.ecocycle.model.local.datastore.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: MainRepository): ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) = repository.login(email, password)
}