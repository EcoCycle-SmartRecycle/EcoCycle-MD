package com.paw.ecocycle.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.paw.ecocycle.model.MainRepository
import com.paw.ecocycle.model.local.datastore.UserModel

class SplashViewModel(private val repository: MainRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}