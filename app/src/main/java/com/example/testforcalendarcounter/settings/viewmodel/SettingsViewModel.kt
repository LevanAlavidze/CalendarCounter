package com.example.testforcalendarcounter.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.data.packprice.PackPrice
import com.example.testforcalendarcounter.repository.Settings.UserSettingsRepository
import com.example.testforcalendarcounter.repository.cigarette.BaselineRepository
import com.example.testforcalendarcounter.repository.packprice.PackPriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val packPriceRepository: PackPriceRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val baselineRepository: BaselineRepository
) : ViewModel() {

    private val _packPrice = MutableLiveData<PackPrice>()
    val packPrice: LiveData<PackPrice> get() = _packPrice

    private val _baselineCigs = MutableLiveData<Int>()
    val baselineCigs: LiveData<Int> get() = _baselineCigs

    private val _baselineSaved = MutableLiveData<Unit>()
    val baselineSaved: LiveData<Unit> get() = _baselineSaved

    init {
        fetchPackPrice()
        fetchBaseLine()
    }

    private fun fetchBaseLine() {
        viewModelScope.launch {
            val settings = userSettingsRepository.getUserSettings()
            _baselineCigs.value = settings?.baselineCigsPerDay ?: 0
        }
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
            fetchPackPrice()  // refresh UI
        }
    }


    fun saveBaseline(baseline: Int) {
        viewModelScope.launch {
            val tz = TimeZone.currentSystemDefault()
            val today = Clock.System.todayIn(tz)

            // 1) History: date-effective baseline (today)
            baselineRepository.setBaselineEffective(today, baseline)

            // 2) Current: update settings so UI shows the value everywhere
            userSettingsRepository.updateBaselineCigsPerDay(baseline)

            // 3) Update screen state
            _baselineCigs.value = baseline
            _baselineSaved.value = Unit
        }
    }

    // Backwards-compatibility with your existing calls
    fun updateBaseLine(baseline: Int) = saveBaseline(baseline)
}
