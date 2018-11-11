package com.orrie.deputychallenge.viewmodels

import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.ShiftUtils
import com.orrie.deputychallenge.utils.autoDispose
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class ShiftsViewModel(
    private val shiftsRepository: ShiftsRepository,
    private val exits: Observable<Unit>,
    private val shiftUtils: ShiftUtils
) {

    private var shifts = listOf<Shift>()

    private val requestLocationPermissionShowsSubject = PublishSubject.create<Unit>()
    val requestLocationPermissionShows: Observable<Unit> = requestLocationPermissionShowsSubject.hide()

    private val shiftsUpdatesSubject = BehaviorSubject.create<List<Shift>>()
    val shiftsUpdates: Observable<List<Shift>> = shiftsUpdatesSubject.hide()

    private val loadingVisibilityChangesSubject = BehaviorSubject.create<Boolean>()
    val loadVisibilityChanges: Observable<Boolean> = loadingVisibilityChangesSubject.hide()

    private val shiftStartConfirmsSubject = PublishSubject.create<Unit>()
    val shiftStartConfirms: Observable<Unit> = shiftStartConfirmsSubject.hide()

    private val shiftEndConfirmsSubject = PublishSubject.create<Unit>()
    val shiftEndConfirms: Observable<Unit> = shiftEndConfirmsSubject.hide()

    private val errorShowsSubject = PublishSubject.create<String>()
    val errorShows: Observable<String> = errorShowsSubject.hide()

    private val shiftInProgressErrorShowsSubject = PublishSubject.create<Unit>()
    val shiftInProgressErrorShows: Observable<Unit> = shiftInProgressErrorShowsSubject.hide()

    init {
        updateShifts()
    }

    fun addShiftClicked() {
        if (shiftInProgress()) {
            shiftInProgressErrorShowsSubject.onNext(Unit)
        } else {
            shiftStartConfirmsSubject.onNext(Unit)
        }
    }

    fun confirmAddShiftClicked() {
        shiftUtils.buildShiftChangeFromCurrentLocationAndTime()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { loadingVisibilityChangesSubject.onNext(true) }
            .doAfterTerminate { loadingVisibilityChangesSubject.onNext(false) }
            .flatMapCompletable { shiftsRepository.startShift(it) }
            .subscribe({
                Timber.d("Successfully started shift")
                updateShifts()
            }, { error ->
                Timber.e(error, "Failed to start shift")
                if (error is SecurityException) {
                    requestLocationPermissionShowsSubject.onNext(Unit)
                } else {
                    error.message?.let { errorShowsSubject.onNext(it) }
                }
            }).autoDispose(exits)
    }

    fun shiftClicked(shift: Shift) {
        if (shift.end.isNullOrBlank()) {
            shiftEndConfirmsSubject.onNext(Unit)
        }
    }

    fun confirmEndShiftClicked() {
        shiftUtils.buildShiftChangeFromCurrentLocationAndTime()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { loadingVisibilityChangesSubject.onNext(true) }
            .doAfterTerminate { loadingVisibilityChangesSubject.onNext(false) }
            .flatMapCompletable { shiftsRepository.endShift(it) }
            .subscribe({
                Timber.d("Successfully ended shift")
                updateShifts()
            }, { error ->
                Timber.e(error, "Failed to end shift")
                if (error is SecurityException) {
                    requestLocationPermissionShowsSubject.onNext(Unit)
                } else {
                    error.message?.let { errorShowsSubject.onNext(it) }
                }
            }).autoDispose(exits)
    }

    private fun shiftInProgress(): Boolean {
        return shifts.firstOrNull { it.end.isNullOrBlank() } != null
    }

    private fun updateShifts() {
        shiftsRepository.getShifts()
            .doOnSubscribe { loadingVisibilityChangesSubject.onNext(true) }
            .doAfterTerminate { loadingVisibilityChangesSubject.onNext(false) }
            .subscribe({ shifts ->
                Timber.d("Shifts are $shifts")
                this.shifts = shifts
                shiftsUpdatesSubject.onNext(shifts)
            }, {
                Timber.e(it, "Failed to retrieve shifts")
            }).autoDispose(exits)
    }
}