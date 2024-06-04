package com.carin.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.carin.data.local.daos.RouteDao
import com.carin.data.local.daos.UserDao
import com.carin.data.local.entities.RouteEntity
import com.carin.data.local.entities.UserEntity

@Database(entities = [
        UserEntity::class,
        RouteEntity::class
    ],
    version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routeDao(): RouteDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }
        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "carin_database.db")
            .build()
    }
}