package com.paw.ecocycle.utils

import android.content.Context
import com.paw.ecocycle.model.MainRepository
import com.paw.ecocycle.model.local.datastore.UserPreference
import com.paw.ecocycle.model.local.datastore.dataStore
import com.paw.ecocycle.model.remote.service.ApiConfig

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(context)
        return MainRepository.getInstance(pref, apiService)
    }
}