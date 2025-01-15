package com.example.testforcalendarcounter.repository.packprice

import com.example.testforcalendarcounter.data.dao.PackPriceDao
import com.example.testforcalendarcounter.data.packprice.PackPrice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PackPriceRepositoryImpl @Inject constructor(
    private val packPriceDao: PackPriceDao
) : PackPriceRepository {

    override suspend fun getPackPrice(): PackPrice? = withContext(Dispatchers.IO) {
        packPriceDao.getPackPrice() ?: PackPrice(price = 0.0, currency = "USD")
    }

    override suspend fun updatePackPrice(price: Double, currency: String) = withContext(Dispatchers.IO) {
        val packPrice = PackPrice(price = price, currency = currency)
        packPriceDao.insertOrUpdatePackPrice(packPrice)
    }

    override suspend fun calculateCostPerCigarette(): Double = withContext(Dispatchers.IO) {
        val packPrice = packPriceDao.getPackPrice()
        // We assume 20 cigarettes in a pack
        packPrice?.price?.div(20) ?: 0.0
    }
}