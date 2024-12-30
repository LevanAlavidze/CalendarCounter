package com.example.testforcalendarcounter.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testforcalendarcounter.data.packprice.PackPrice


@Dao
interface PackPriceDao {
    @Query("SELECT * FROM pack_price LIMIT 1")
    suspend fun getPackPrice(): PackPrice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePackPrice(packPrice: PackPrice)
}
