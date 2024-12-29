package com.example.testforcalendarcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class PackPrice(val price: Double, val currency: String)

class SettingsViewModel : ViewModel() {

    private val _packPrice = MutableLiveData(PackPrice(0.0, "USD"))
    val packPrice: LiveData<PackPrice> get() = _packPrice

    fun updatePackPrice(price: Double, currency: String) {
        _packPrice.value = PackPrice(price, currency)
    }
}
