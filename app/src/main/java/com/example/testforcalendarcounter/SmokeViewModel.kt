package com.example.testforcalendarcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.timer.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmokeViewModel @Inject constructor(
    private val repository: CigaretteRepository,
    private val timerManager: TimerManager
): ViewModel() {

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

    init {
        refreshCounts()
        loadTimerState()
        fetchLastTenCigarettes()
        fetchCurrency()
    }

    // Refresh counters and costs
    fun refreshCounts() {
        viewModelScope.launch {
            _dayCigaretteCount.value = repository.getDailyCount()
            _weekCigaretteCount.value = repository.getWeeklyCount()
            _monthCigaretteCount.value = repository.getMonthlyCount()
            _dailyCost.value = repository.calculateDailyCost()
            _weeklyCost.value = repository.calculateWeeklyCost()
            _monthlyCost.value = repository.calculateMonthlyCost()
        }
    }

    // Timer
    fun loadTimerState() {
        viewModelScope.launch {
            val timer = repository.getTimer()
            if (timer != null && timer.isRunning) {
                timerManager.resumeTimer(timer.startTime) { formattedTime ->
                    _timer.postValue(formattedTime)
                }
            } else {
                _timer.value = "Timer: 00:00:00"
            }
        }
    }

    fun addCigarette() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            timerManager.resetAndStartTimer { formattedTime ->
                _timer.postValue(formattedTime)
            }
            repository.saveTimer(Timer(startTime = startTime, isRunning = true))
            repository.addCigarette()
            refreshCounts()
            fetchCurrency()   // To reflect changes in cost if currency changed
            fetchLastTenCigarettes()
        }
    }

    fun deleteCigarette(entry: CigaretteEntry) {
        viewModelScope.launch {
            repository.deleteCigarette(entry)
            refreshCounts()
            fetchLastTenCigarettes()
        }
    }

    // Observing costs
    fun calculateDailyCost() {
        viewModelScope.launch {
            _dailyCost.value = repository.calculateDailyCost()
        }
    }

    fun calculateWeeklyCost() {
        viewModelScope.launch {
            _weeklyCost.value = repository.calculateWeeklyCost()
        }
    }

    fun calculateMonthlyCost() {
        viewModelScope.launch {
            _monthlyCost.value = repository.calculateMonthlyCost()
        }
    }

    // Last ten cigarettes
    fun fetchLastTenCigarettes() {
        viewModelScope.launch {
            _lastTenCigarettes.value = repository.getLastTenCigarettes()
        }
    }

    // Currency
    fun fetchCurrency() {
        viewModelScope.launch {
            val packPrice = repository.getPackPrice()
            _currency.postValue(packPrice?.currency ?: "USD")
        }
    }
}
