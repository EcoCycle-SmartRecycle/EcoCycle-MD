package com.paw.ecocycle.model

import android.util.Log
import androidx.lifecycle.liveData
import com.paw.ecocycle.model.local.datastore.UserModel
import com.paw.ecocycle.model.local.datastore.UserPreference
import com.paw.ecocycle.model.remote.request.LoginRequest
import com.paw.ecocycle.model.remote.request.RegisterRequest
import com.paw.ecocycle.model.remote.service.ApiService
import com.paw.ecocycle.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class MainRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {

    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val request = LoginRequest(email = email, password = password)
            val successResponse = apiService.login(request)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            Log.d(TAG, "login: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val request = RegisterRequest(name = name, email = email, password = password)
            val successResponse = apiService.register(request)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            Log.d(TAG, "register: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun postImage(imageFile: File) = liveData {
        emit(ResultState.Loading)
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse =
                apiService.postImage(token = "Bearer ${getSession().first().token}", multipartBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            Log.d(TAG, "post: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getImages() = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse =
                apiService.getImages(token = "Bearer ${getSession().first().token}")
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            Log.d(TAG, "get: ${e.message.toString()}")
            emit(ResultState.Error(e.message.toString()))
        }
    }
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        private const val TAG = "MainRepository"

        @Volatile
        private var instance: MainRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ): MainRepository =
            instance ?: synchronized(this) {
                instance ?: MainRepository(userPreference, apiService)
            }.also { instance = it }
    }
}