package com.carin.di

import android.content.Context
import com.carin.data.local.AppDatabase
import com.carin.data.repositories.RouteRepository
import com.carin.data.repositories.UserRepository
import com.carin.data.repositories.VehicleRepository

object RepositoryModule {
    fun provideUserRepository(context: Context): UserRepository {
        val userDao = AppDatabase.invoke(context).userDao()
        val userService = NetworkModule.provideUserService(context)
        val authService = NetworkModule.provideAuthService(context)
        return UserRepository(userDao, userService, authService)
    }

    fun provideRouteRepository(context: Context): RouteRepository {
        val routeDao = AppDatabase.invoke(context).routeDao()
        val userDao = AppDatabase.invoke(context).userDao()
        val vehicleDao = AppDatabase.invoke(context).vehicleDao()
        val routeService = NetworkModule.provideRouteService(context)
        val userService = NetworkModule.provideUserService(context)
        val vehicleService = NetworkModule.provideVehicleService(context)
        return RouteRepository(routeDao, userDao, vehicleDao, routeService, userService, vehicleService)
    }

    fun provideVehicleRepository(context: Context): VehicleRepository {
        val vehicleDao = AppDatabase.invoke(context).vehicleDao()
        val vehicleService = NetworkModule.provideVehicleService(context)
        return VehicleRepository(vehicleDao, vehicleService)
    }
}