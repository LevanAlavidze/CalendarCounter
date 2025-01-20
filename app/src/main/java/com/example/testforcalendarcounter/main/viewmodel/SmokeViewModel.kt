package com.example.testforcalendarcounter.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.UserSettings
import com.example.testforcalendarcounter.data.timer.Timer
import com.example.testforcalendarcounter.data.timer.TimerManager
import com.example.testforcalendarcounter.enums.MoodLevel
import com.example.testforcalendarcounter.repository.Settings.UserSettingsRepository
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
    private val timerManager: TimerManager,
    private val userSettingsRepository: UserSettingsRepository,
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

    private val _dailySavings = MutableLiveData<Double>()
    val dailySavings: LiveData<Double> = _dailySavings

    private val _moodLevel = MutableLiveData<MoodLevel>()
    val moodLevel: LiveData<MoodLevel> = _moodLevel

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
            // 1) Basic counts from CigaretteRepository
            val dayCount = cigaretteRepository.getDailyCount()
            val weekCount = cigaretteRepository.getWeeklyCount()
            val monthCount = cigaretteRepository.getMonthlyCount()

            // 2) Cost calculations from StatsRepository
            val dailyCostPair = statsRepository.calculateDailyCost()    // e.g., (count, cost)
            val weeklyCostPair = statsRepository.calculateWeeklyCost()  // e.g., (count, cost)
            val monthlyCostPair = statsRepository.calculateMonthlyCost()// e.g., (count, cost)

            // 3) Baseline & savings calculation
            //    a) current daily usage (already have it: dayCount)
            //    b) baseline from DB (UserSettings table or repo)
            val userSettings = userSettingsRepository.getUserSettings()
            val baselineCigs = userSettings?.baselineCigsPerDay ?: 0

            //    c) cigsSaved = (baseline - currentDailyCount) if positive
            val cigsSaved = (baselineCigs - dayCount).coerceAtLeast(0)

            //    d) costPerCig can be from your PackPriceRepository or StatsRepository
            //       (whichever is your single source of truth).
            val costPerCig = packPriceRepository.calculateCostPerCigarette()
            val moneySaved = cigsSaved * costPerCig

            // 4) Post results to LiveData
            _dayCigaretteCount.value = dayCount
            _weekCigaretteCount.value = weekCount
            _monthCigaretteCount.value = monthCount

            _dailyCost.value = dailyCostPair
            _weeklyCost.value = weeklyCostPair
            _monthlyCost.value = monthlyCostPair

            _dailySavings.value = moneySaved


            val mood = computedMoodLevel(baselineCigs,dayCount)
            _moodLevel.postValue(mood)
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

    fun computedMoodLevel(
        baseline: Int,
        currentDailyCount: Int
    ): MoodLevel {
        if (baseline <=0){
            return MoodLevel.VERY_GOOD
        }

        val segmentSize = baseline/5.0

        val ratio = currentDailyCount / segmentSize
        val segmentIndex = ratio.toInt()

        val clampedSegment = segmentIndex.coerceIn(0,4)

        return when (clampedSegment){
            0 -> MoodLevel.VERY_GOOD
            1 -> MoodLevel.GOOD
            2 -> MoodLevel.FINE
            3 -> MoodLevel.BAD
            else -> MoodLevel.VERY_BAD
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
