package com.example.testforcalendarcounter.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.data.packprice.PackPrice
import com.example.testforcalendarcounter.repository.Settings.UserSettingsRepository
import com.example.testforcalendarcounter.repository.packprice.PackPriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val packPriceRepository: PackPriceRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _packPrice = MutableLiveData<PackPrice>()
    val packPrice: LiveData<PackPrice> get() = _packPrice

    private val _baselineCigs = MutableLiveData<Int>()
    val baselineCigs: LiveData<Int> get() = _baselineCigs

    init {
        fetchPackPrice()
        fetchBaseLine()
    }

    private fun fetchBaseLine() {
        viewModelScope.launch{
            val settings = userSettingsRepository.getUserSettings()
            _baselineCigs.value = settings?.baselineCigsPerDay ?:0
        }
    }

    private fun fetchPackPrice() {
        viewModelScope.launch {
            val price = packPriceRepository.getPackPrice()
                ?: PackPrice(price = 0.0, currency = "USD")
            _packPrice.postValue(price)
        }
    }

    fun updateBaseLine(baseline: Int){
        viewModelScope.launch{
            userSettingsRepository.updateBaselineCigsPerDay(baseline)
            fetchBaseLine()
        }
    }

    fun updatePackPrice(price: Double, currency: String) {
        viewModelScope.launch {
            packPriceRepository.updatePackPrice(price, currency)
            fetchPackPrice()  // Re-fetch to update UI
        }
    }
}
