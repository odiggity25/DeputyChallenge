package com.orrie.deputychallenge.viewmodels

import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.autoDispose
import io.reactivex.Observable
import timber.log.Timber

class ShiftsViewModel(
    private val shiftsRepository: ShiftsRepository,
    private val exits: Observable<Unit>
) {

    init {
        shiftsRepository.getShifts()
            .subscribe({
                Timber.d("Shifts are $it")
            }, {
                Timber.e(it, "Failed to retrieve shifts")
            }).autoDispose(exits)
    }
}