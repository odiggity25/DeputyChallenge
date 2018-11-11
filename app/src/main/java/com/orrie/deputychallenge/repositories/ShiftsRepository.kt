package com.orrie.deputychallenge.repositories

import com.orrie.deputychallenge.apis.ShiftsApi
import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.models.ShiftChange
import io.reactivex.Completable
import io.reactivex.Single

/**
 * The repository that manages the shifts. In this simple example it is not
 * that useful, however if we were to add caching or a database layer this would
 * co-ordinate that
 */
class ShiftsRepository(
    private val shiftsApi: ShiftsApi
) {

    fun getShifts(): Single<List<Shift>> {
        return shiftsApi.getShifts()
    }

    fun startShift(shiftChange: ShiftChange): Completable {
        return shiftsApi.startShift(shiftChange)
    }

    fun endShift(shiftChange: ShiftChange): Completable {
        return shiftsApi.endShift(shiftChange)
    }
}