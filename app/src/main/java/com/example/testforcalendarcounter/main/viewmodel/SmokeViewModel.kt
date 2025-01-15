package com.example.testforcalendarcounter.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.timer.Timer
import com.example.testforcalendarcounter.data.timer.TimerManager
import com.example.testforcalendarcounter.repository.cigarette.CigaretteRepository
import com.example.testforcalendarcounter.repository.packprice.PackPriceRepository
import com.example.testforcalendarcounter.repository.stats.StatsRepository
import com.example.testforcalendarcounter.repository.timer.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmokeViewModel @Inject constructor(
    private val cigaretteRepository: CigaretteRepository,
    private val statsRepository: StatsRepository,         // For cost/stats
    private val timerRepository: TimerRepository,         // For timer CRUD
    private val packPriceRepository: PackPriceRepository, // For pack price & currency
    private val timerManager: TimerManager
) : ViewModel() {

    // region LiveData Exposed to UI
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String> get() = _currency

    private val _dayCigaretteCount = MutableLiveData<Int>()
    val dayCigaretteCount: LiveData<Int> = _dayCigaretteCount

    private val _weekCigaretteCount = MutableLiveData<Int>()
    val weekCigaretteCount: LiveData<Int> = _weekCigaretteCount

    private val _monthCigaretteCount = MutableLiveData<Int>()
    val monthCigaretteCount: LiveData<Int> = _monthCigaretteCount

    private val _dailyCost = MutableLiveData<Pair<Int, Double>>()
    val dailyCost: LiveData<Pair<Int, Double>> get() = _dailyCost

    private val _weeklyCost = MutableLiveData<Pair<Int, Double>>()
    val weeklyCost: LiveData<Pair<Int, Double>> get() = _weeklyCost

    private val _monthlyCost = MutableLiveData<Pair<Int, Double>>()
    val monthlyCost: LiveData<Pair<Int, Double>> get() = _monthlyCost

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String> = _timer

    private val _lastTenCigarettes = MutableLiveData<List<CigaretteEntry>>()
    val lastTenCigarettes: LiveData<List<CigaretteEntry>> = _lastTenCigarettes
    // endregion

    init {
        refreshCounts()
        loadTimerState()
        fetchLastTenCigarettes()
        fetchCurrency()
    }

    // region Counters & Costs
    fun refreshCounts() {
        viewModelScope.launch {
            // Basic counts from CigaretteRepository
            _dayCigaretteCount.value = cigaretteRepository.getDailyCount()
            _weekCigaretteCount.value = cigaretteRepository.getWeeklyCount()
            _monthCigaretteCount.value = cigaretteRepository.getMonthlyCount()

            // Cost calculations from StatsRepository
            _dailyCost.value = statsRepository.calculateDailyCost()
            _weeklyCost.value = statsRepository.calculateWeeklyCost()
            _monthlyCost.value = statsRepository.calculateMonthlyCost()
        }
    }
    // endregion

    // region Timer
    fun loadTimerState() {
        viewModelScope.launch {
            val timer = timerRepository.getTimer()
            if (timer != null && timer.isRunning) {
                // Resume existing timer
                timerManager.resumeTimer(timer.startTime) { formattedTime ->
                    _timer.postValue(formattedTime)
                }
            } else {
                // If no timer or not running
                _timer.value = "Timer: 00:00:00"
            }
        }
    }

    fun addCigarette() {
        viewModelScope.launch {
            // Reset and start a new timer
            val startTime = System.currentTimeMillis()
            timerManager.resetAndStartTimer { formattedTime ->
                _timer.postValue(formattedTime)
            }
            // Save new Timer in DB
            timerRepository.saveTimer(Timer(startTime = startTime, isRunning = true))

            // Add a cigarette
            cigaretteRepository.addCigarette()

            // Update UI
            refreshCounts()
            fetchCurrency()
            fetchLastTenCigarettes()
        }
    }

    fun deleteCigarette(entry: CigaretteEntry) {
        viewModelScope.launch {
            cigaretteRepository.deleteCigarette(entry)
            refreshCounts()
            fetchLastTenCigarettes()
        }
    }
    // endregion

    // region LastTenCigarettes
    fun fetchLastTenCigarettes() {
        viewModelScope.launch {
            _lastTenCigarettes.value = cigaretteRepository.getLastTenCigarettes()
        }
    }
    // endregion

    // region Pack Price / Currency
    fun fetchCurrency() {
        viewModelScope.launch {
            val packPrice = packPriceRepository.getPackPrice()
            _currency.postValue(packPrice?.currency ?: "USD")
        }
    }
    // endregion
}
