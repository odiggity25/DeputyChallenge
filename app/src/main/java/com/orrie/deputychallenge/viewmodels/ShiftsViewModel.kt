package com.orrie.deputychallenge.viewmodels

import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.autoDispose
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

class ShiftsViewModel(
    private val shiftsRepository: ShiftsRepository,
    private val exits: Observable<Unit>
) {

    private val shiftsUpdatesSubject = BehaviorSubject.create<List<Shift>>()
    val shiftsUpdates = shiftsUpdatesSubject.hide()

    init {
        shiftsRepository.getShifts()
            .subscribe({     shifts ->
                Timber.d("Shifts are $shifts")
                shiftsUpdatesSubject.onNext(shifts)
            }, {
                Timber.e(it, "Failed to retrieve shifts")
            }).autoDispose(exits)


        val shift1 = Shift(1, "May 3, 2018", "May 4, 2018", 23.00F, 24.00F, 23.00F, 24.00F, "https://unsplash.it/500/500/?random")
        val shift2 = Shift(1, "May 5, 2018", "May 5, 2018", 23.00F, 24.00F, 23.00F, 24.00F, "https://unsplash.it/500/500/?random")

        shiftsUpdatesSubject.onNext(listOf(shift1, shift2))
    }
}