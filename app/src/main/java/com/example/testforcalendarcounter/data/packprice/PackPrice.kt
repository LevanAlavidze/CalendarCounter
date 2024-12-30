package com.example.testforcalendarcounter.data.packprice

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pack_price")
data class PackPrice(
    @PrimaryKey val id: Double = 0.0,
    val price: Double,
    val currency: String
)