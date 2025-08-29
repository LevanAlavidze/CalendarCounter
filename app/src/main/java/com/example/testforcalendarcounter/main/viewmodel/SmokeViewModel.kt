package com.example.testforcalendarcounter.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.data.entity.CigaretteEntry
import com.example.testforcalendarcounter.data.timer.Timer
import com.example.testforcalendarcounter.data.timer.TimerManager
import com.example.testforcalendarcounter.repository.Settings.UserSettingsRepository
import com.example.testforcalendarcounter.repository.cigarette.BaselineRepository
import com.example.testforcalendarcounter.repository.cigarette.CigaretteRepository
import com.example.testforcalendarcounter.repository.packprice.PackPriceRepository
import com.example.testforcalendarcounter.repository.stats.StatsRepository
import com.example.testforcalendarcounter.repository.timer.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class SmokeViewModel @Inject constructor(
    private val cigaretteRepository: CigaretteRepository,
    private val statsRepository: StatsRepository,         // For cost/stats
    private val timerRepository: TimerRepository,         // For timer CRUD
    private val packPriceRepository: PackPriceRepository, // For pack price & currency
    private val timerManager: TimerManager,
    private val userSettingsRepository: UserSettingsRepository,
    private val baselineRepository: BaselineRepository,
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

    private val _lifetimeNotSmoked = MutableLiveData<Long>()
    val lifetimeNotSmoked: LiveData<Long> = _lifetimeNotSmoked


    /*private val _moodLevel = MutableLiveData<MoodLevel>()
    val moodLevel: LiveData<MoodLevel> = _moodLevel*/

    private val _lastTenCigarettes = MutableLiveData<List<CigaretteEntry>>()
    val lastTenCigarettes: LiveData<List<CigaretteEntry>> = _lastTenCigarettes
    // endregion

    private val _dailySavingsPercent = MutableLiveData<Int>()
    val dailySavingsPercent: LiveData<Int> = _dailySavingsPercent

    private fun tz() = TimeZone.currentSystemDefault()

    init {
        refreshCounts()
        loadTimerState()
        fetchLastTenCigarettes()
        fetchCurrency()
        refreshLifetimeNotSmoked()
       /* viewModelScope.launch { rollIfNeeded() }*/
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

            val consumptionPercent  = if (baselineCigs > 0) {
                ((dayCount.toFloat() / baselineCigs) * 100).toInt().coerceIn(0,100)
            } else {
                0
            }


            // 4) Post results to LiveData
            _dayCigaretteCount.postValue(dayCount)
            _weekCigaretteCount.postValue(weekCount)
            _monthCigaretteCount.postValue(monthCount)

            _dailyCost.postValue(dailyCostPair)
            _weeklyCost.postValue(weeklyCostPair)
            _monthlyCost.postValue(monthlyCostPair)

            _dailySavings.postValue(moneySaved)

            _dailySavingsPercent.postValue(consumptionPercent)



            // mood logic …
            /*_moodLevel.postValue(computedMoodLevel(baselineCigs, dayCount))*/
        }
    }
    // endregion

    fun refreshLifetimeNotSmoked() {
        viewModelScope.launch {
            val tz = TimeZone.currentSystemDefault()
            val today = Clock.System.todayIn(tz)

            val firstLog = cigaretteRepository.getEarliestDate() ?: today
            val firstBaseline = baselineRepository.earliestEffective() ?: today
            val start = if (firstLog < firstBaseline) firstLog else firstBaseline

            // Counts per day in [start, today)
            val byDay = cigaretteRepository.getCountBetweenDatesList(start, today)

            // Effective-dated segments
            val segments = baselineRepository.getAll().sortedBy { it.effectiveDate }

            // Baseline effective at 'start' (or 0 if none)
            var segIdx = segments.indexOfLast { it.effectiveDate <= start }
            var currentBaseline = if (segIdx >= 0) segments[segIdx].baseline else 0

            var total = 0L
            var d = start
            // IMPORTANT: stop at yesterday -> d < today
            while (d < today) {
                // advance segment if a new baseline becomes effective on/before d
                while (segIdx + 1 < segments.size && segments[segIdx + 1].effectiveDate <= d) {
                    segIdx++
                    currentBaseline = segments[segIdx].baseline
                }
                val smoked = byDay[d] ?: 0
                total += (currentBaseline - smoked).coerceAtLeast(0)
                d = d.plus(1, DateTimeUnit.DAY)
            }

            _lifetimeNotSmoked.postValue(total)
        }
    }



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
            refreshLifetimeNotSmoked()
            fetchCurrency()
            fetchLastTenCigarettes()
        }
    }

    /*suspend fun rollIfNeeded(now: Instant = Clock.System.now()) {
        val today = now.toLocalDateTime(tz()).date

        lifetimeRepository.ensureRow()
        val lastRolled = lifetimeRepository.getLastRolledDate()

        // First run: start tracking from today, don’t roll anything
        if (lastRolled == null) {
            lifetimeRepository.setLastRolledDate(today)
            _lifetimeNotSmoked.postValue(lifetimeRepository.getLifetimeNotSmoked())
            return
        }

        // Already rolled today
        if (lastRolled >= today) {
            _lifetimeNotSmoked.postValue(lifetimeRepository.getLifetimeNotSmoked())
            return
        }

        val baseline = userSettingsRepository.getUserSettings()?.baselineCigsPerDay ?: 0
        var d: LocalDate = lastRolled
        var add = 0L

        while (d < today) {
            val smoked = cigaretteRepository.getDailyCount(d)
            val notSmoked = (baseline - smoked).coerceAtLeast(0)
            add += notSmoked.toLong()
            d = d.plus(1, DateTimeUnit.DAY)
        }

        if (add > 0) lifetimeRepository.addToLifetime(add)
        lifetimeRepository.setLastRolledDate(today)
        _lifetimeNotSmoked.postValue(lifetimeRepository.getLifetimeNotSmoked())
    }*/

/*    fun computedMoodLevel(
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


    }*/


    fun deleteCigarette(entry: CigaretteEntry) {
        viewModelScope.launch {
            // 1) Actually delete the cigarette
            cigaretteRepository.deleteCigarette(entry)

            // 2) Fetch the new "last smoked" entry
            val updatedLastEntries = cigaretteRepository.getLastTenCigarettes()
            val newLastEntry = updatedLastEntries.firstOrNull()

            if (newLastEntry != null) {
                // We have at least one entry left in DB.
                // Make sure timer is set to start from newLastEntry's timestamp
                val newStartTime = newLastEntry.timestamp

                // 2a) Save new Timer to DB
                timerRepository.saveTimer(Timer(startTime = newStartTime, isRunning = true))

                // 2b) Reset the running timer in your TimerManager so it
                //     counts up from newStartTime

            } else {
                // 3) If there are NO entries left, we can stop the timer
                timerRepository.saveTimer(Timer(startTime = 0, isRunning = false))
                _timer.postValue("Timer: 00:00:00")
            }

            // 4) Update UI
            refreshCounts()
            refreshLifetimeNotSmoked()
            fetchLastTenCigarettes()
            loadTimerState()

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
