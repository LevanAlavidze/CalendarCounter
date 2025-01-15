package com.example.testforcalendarcounter.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.data.packprice.PackPrice
import com.example.testforcalendarcounter.repository.packprice.PackPriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val packPriceRepository: PackPriceRepository
) : ViewModel() {

    private val _packPrice = MutableLiveData<PackPrice>()
    val packPrice: LiveData<PackPrice> get() = _packPrice

    init {
        fetchPackPrice()
    }

    private fun fetchPackPrice() {
        viewModelScope.launch {
            val price = packPriceRepository.getPackPrice()
                ?: PackPrice(price = 0.0, currency = "USD")
            _packPrice.postValue(price)
        }
    }

    fun updatePackPrice(price: Double, currency: String) {
        viewModelScope.launch {
            packPriceRepository.updatePackPrice(price, currency)
            fetchPackPrice()  // Re-fetch to update UI
        }
    }
}
