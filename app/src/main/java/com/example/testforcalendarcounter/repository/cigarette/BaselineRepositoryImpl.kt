package com.example.testforcalendarcounter.repository.cigarette

import com.example.testforcalendarcounter.data.dao.BaselineDao
import com.example.testforcalendarcounter.data.dao.CigaretteDao
import com.example.testforcalendarcounter.data.entity.BaselineHistory
import com.example.testforcalendarcounter.repository.settings.UserSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class BaselineRepositoryImpl @Inject constructor(
    private val baselineDao: BaselineDao,
    private val settingsRepo: UserSettingsRepository,
    private val cigaretteDao: CigaretteDao,
    private val io: CoroutineDispatcher
) : BaselineRepository {
    private val bootstrapMutex = Mutex()
    @Volatile private var bootstrapped = false

    override suspend fun setBaselineEffective(date: LocalDate, baseline: Int) {
        baselineDao.upsert(BaselineHistory(date, baseline))
    }

    override suspend fun baselineFor(date: LocalDate): Int {
        ensureBootstrapped()

        val found: Int? = baselineDao.getBaselineForDate(date)
        val legacy: Int = settingsRepo.getLegacyBaseline()
        return found ?: legacy
    }

    override suspend fun getAll(): List<BaselineHistory> = baselineDao.getAll()
    override suspend fun earliestEffective(): LocalDate? = baselineDao.getEarliestEffective()

    override suspend fun ensureBootstrapped() {
        if (bootstrapped) return
        bootstrapMutex.withLock {
            if (bootstrapped) return

            withContext(io) {
                // 1) What’s the earliest day we have any smoke record?
                val earliestSmoke: LocalDate? = cigaretteDao.getEarliestSmokeDate()
                if (earliestSmoke == null) {
                    // No data to cover; nothing to do
                    bootstrapped = true
                    return@withContext
                }

                // 2) What’s the earliest effective baseline row we already have?
                val earliestEffective: LocalDate? = baselineDao.getEarliestEffective()
                val legacy: Int = settingsRepo.getLegacyBaseline()

                when {
                    // Table empty → insert one row at earliestSmoke
                    earliestEffective == null -> {
                        baselineDao.upsert(
                            BaselineHistory(
                                effectiveDate = earliestSmoke,
                                baseline = legacy
                            )
                        )
                    }

                    // We already have some rows, but the earliest baseline starts AFTER data began
                    earliestEffective > earliestSmoke -> {
                        // Insert a covering row so *all* older days are considered
                        baselineDao.upsert(
                            BaselineHistory(
                                effectiveDate = earliestSmoke,
                                baseline = legacy
                            )
                        )
                    }

                    // else: we already have a baseline row on/before the earliest smoke date → nothing to do
                }
            }

            bootstrapped = true
        }
    }

}
