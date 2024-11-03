package com.example.weather.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val location:String,
    val latitude: Double,
    val longitude: Double,
    val alertDate: Date,
    val alertTime: String,
    val notificationType: String
)
