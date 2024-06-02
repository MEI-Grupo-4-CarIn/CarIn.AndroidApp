package com.carin.di

import android.content.Context
import com.carin.data.local.AppDatabase
import com.carin.data.repositories.UserRepository

object RepositoryModule {
    fun provideUserRepository(context: Context): UserRepository {
        val userDao = AppDatabase.invoke(context).userDao()
        val userService = NetworkModule.userService
        val userService = NetworkModule.provideUserService(context)
        return UserRepository(userDao, userService)
    }
}