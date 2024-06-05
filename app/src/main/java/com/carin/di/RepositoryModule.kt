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
        return UserRepository(userDao, userService)
    }

    fun provideRouteRepository(context: Context): RouteRepository {
        val routeDao = AppDatabase.invoke(context).routeDao()
        val userDao = AppDatabase.invoke(context).userDao()
        val routeService = NetworkModule.provideRouteService(context)
        val userService = NetworkModule.provideUserService(context)
        return RouteRepository(routeDao, userDao, routeService, userService)
    }

    fun provideVehicleRepository(context: Context): VehicleRepository {
        val vehicleDao = AppDatabase.invoke(context).vehicleDao()
        val vehicleService = NetworkModule.provideVehicleService(context)
        return VehicleRepository(vehicleDao, vehicleService)
    }
}