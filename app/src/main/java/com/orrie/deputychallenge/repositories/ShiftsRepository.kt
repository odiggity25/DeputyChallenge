package com.orrie.deputychallenge.repositories

import com.orrie.deputychallenge.apis.ShiftsApi
import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.models.ShiftChange
import io.reactivex.Completable
import io.reactivex.Single

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