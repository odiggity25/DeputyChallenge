package com.orrie.deputychallenge.repositories

import com.orrie.deputychallenge.apis.ShiftsApi
import com.orrie.deputychallenge.models.Shift
import io.reactivex.Single

class ShiftsRepository(
    private val shiftsApi: ShiftsApi
) {

    var cachedShifts: MutableList<Shift>? = null

    fun getShifts(): Single<List<Shift>> {
        return shiftsApi.getShifts()
    }
}