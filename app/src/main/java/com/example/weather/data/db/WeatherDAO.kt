package com.example.weather.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.weather.data.WeatherDisplayable

@Dao
interface WeatherDAO {
    @Query("SELECT * FROM weatherLoc WHERE id != 0  ")
    fun getAllFavLocations():Flow<List<WeatherDisplayable>>

    // Get the latest cached location with a specific identifier (e.g., id = 0)
    @Query("SELECT * FROM weatherLoc WHERE id = 0 LIMIT 1")
    fun getCachedLocation(): Flow<WeatherDisplayable?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: WeatherDisplayable)

    @Delete
    suspend fun deleteLocation(location: WeatherDisplayable)
}