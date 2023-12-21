package com.paw.ecocycle.view.viewmodel

import androidx.lifecycle.ViewModel
import com.paw.ecocycle.model.MainRepository

class SignupViewModel(private val repository: MainRepository): ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}