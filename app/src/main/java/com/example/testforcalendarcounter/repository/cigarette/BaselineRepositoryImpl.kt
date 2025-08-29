package com.example.testforcalendarcounter.repository.cigarette

import com.example.testforcalendarcounter.data.dao.BaselineDao
import com.example.testforcalendarcounter.data.entity.BaselineHistory
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class BaselineRepositoryImpl @Inject constructor(
    private val dao: BaselineDao
) : BaselineRepository {
    override suspend fun setBaselineEffective(date: LocalDate, baseline: Int) {
        // Seed an origin row if table empty (covers all dates in the past)
        if (dao.count() == 0) {
            dao.upsert(BaselineHistory(LocalDate(1970, 1, 1), baseline))
        }
        dao.upsert(BaselineHistory(date, baseline))

    }
    override suspend fun baselineFor(date: LocalDate): Int =
        dao.getBaselineForDate(date)?.baseline ?: 0
    override suspend fun getAll(): List<BaselineHistory> = dao.getAll()
    override suspend fun earliestEffective(): LocalDate? = dao.getEarliestEffective()
}