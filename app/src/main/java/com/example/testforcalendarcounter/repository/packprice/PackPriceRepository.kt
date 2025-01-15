package com.example.testforcalendarcounter.repository.packprice

import com.example.testforcalendarcounter.data.packprice.PackPrice

interface PackPriceRepository {
    suspend fun getPackPrice(): PackPrice?
    suspend fun updatePackPrice(price: Double, currency: String)
    suspend fun calculateCostPerCigarette(): Double
}