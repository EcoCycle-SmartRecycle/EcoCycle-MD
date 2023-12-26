package com.paw.ecocycle.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.paw.ecocycle.model.MainRepository
import com.paw.ecocycle.model.local.datastore.UserModel
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val repository: MainRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun postImage(file: File) = repository.postImage(file)

    fun getImages() = repository.getImages()
}