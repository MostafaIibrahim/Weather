package com.example.weather.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
@Database(entities = arrayOf(Product::class), version = 2)
abstract class WeatherDataBase: RoomDatabase() {
    abstract fun getProductDao():ProductDao
    companion object{
        @Volatile
        private var INSTANCE:ProductDataBase? = null
        fun getInstance(ctx: Context):ProductDataBase{
            return INSTANCE ?: synchronized(this){
                val instance:ProductDataBase = Room.databaseBuilder(
                    ctx.applicationContext,ProductDataBase::class.java,"product_db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance }
        }
    }
}*/