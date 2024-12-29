package com.example.testforcalendarcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.data.dao.timer.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmokeViewModel @Inject constructor(
    private val repository: CigaretteRepository,
    private val timerManager: TimerManager
): ViewModel(){

    private val _dayCigaretteCount = MutableLiveData<Int>()
    val dayCigaretteCount: LiveData<Int> = _dayCigaretteCount

    private val _weekCigaretteCount = MutableLiveData<Int>()
    val weekCigaretteCount: LiveData<Int> = _weekCigaretteCount

    private val _monthCigaretteCount = MutableLiveData<Int>()
    val monthCigaretteCount: LiveData<Int> = _monthCigaretteCount

    private val _timer = MutableLiveData<String>()
    val timer: LiveData<String> = _timer

    private val _lastTenCigarettes = MutableLiveData<List<CigaretteEntry>>()
    val lastTenCigarettes: LiveData<List<CigaretteEntry>> = _lastTenCigarettes

    init {
        loadTimerState()
        fetchLastTenCigarettes()
    }

    fun refreshCounts() {
        viewModelScope.launch {
            _dayCigaretteCount.value = repository.getDailyCount()
            _weekCigaretteCount.value = repository.getWeeklyCount()
            _monthCigaretteCount.value = repository.getMonthlyCount()
        }
    }

    fun addCigarette() {
        viewModelScope.launch {
            // Timer logic
            val startTime = System.currentTimeMillis()
            timerManager.resetAndStartTimer { formattedTime ->
                _timer.postValue(formattedTime)
            }
            repository.saveTimer(Timer(startTime = startTime, isRunning = true))

            // Insert a new row
            repository.addCigarette()

            // Refresh counters
            refreshCounts()

            // Re-fetch the last ten cigarettes -> updates RecyclerView
            fetchLastTenCigarettes()
        }
    }

/*    fun addCigarette() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            timerManager.resetAndStartTimer { formattedTime ->
                _timer.postValue(formattedTime)
            }
            repository.saveTimer(Timer(startTime = startTime, isRunning = true))
            repository.addCigarette()
            refreshCounts()
        }

    }*/
    fun loadTimerState() {
        viewModelScope.launch {
            val timer = repository.getTimer()
            if (timer != null && timer.isRunning) {
                timerManager.resumeTimer(timer.startTime) { formattedTime ->
                    _timer.postValue(formattedTime)
                }
            } else {
                // If no timer or not running, show default
                _timer.value = "Timer: 00:00:00"
            }
        }
    }

    fun fetchLastTenCigarettes() {
        viewModelScope.launch {
            _lastTenCigarettes.value = repository.getLastTenCigarettes()
        }
    }

    fun deleteCigarette(entry: CigaretteEntry) {
        viewModelScope.launch {
            repository.deleteCigarette(entry)
            fetchLastTenCigarettes() // Refresh list
            refreshCounts()
        }
    }


}