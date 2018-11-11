package com.orrie.deputychallenge.viewmodels

import android.annotation.SuppressLint
import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.models.ShiftChange
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.DateUtils
import com.orrie.deputychallenge.utils.LocationManager
import com.orrie.deputychallenge.utils.autoDispose
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class ShiftsViewModel(
    private val shiftsRepository: ShiftsRepository,
    private val locationManager: LocationManager,
    private val exits: Observable<Unit>,
    private val dateUtils: DateUtils = DateUtils()
) {

    private var shifts = listOf<Shift>()

    private val requestLocationPermissionShowsSubject = PublishSubject.create<Unit>()
    val requestLocationPermissionShows: Observable<Unit> = requestLocationPermissionShowsSubject.hide()

    private val shiftsUpdatesSubject = BehaviorSubject.create<List<Shift>>()
    val shiftsUpdates = shiftsUpdatesSubject.hide()

    private val loadingVisibilityChangesSubject = BehaviorSubject.create<Boolean>()
    val loadVisibilityChanges: Observable<Boolean> = loadingVisibilityChangesSubject.hide()

    private val shiftStartConfirmsSubject = PublishSubject.create<Unit>()
    val shiftStartConfirms: Observable<Unit> = shiftStartConfirmsSubject.hide()

    private val shiftEndConfirmsSubject = PublishSubject.create<Unit>()
    val shiftEndConfirms: Observable<Unit> = shiftEndConfirmsSubject.hide()

    private val errorShowsSubject = PublishSubject.create<String>()
    val errorShows: Observable<String> = errorShowsSubject.hide()

    private val cantStartNewShiftWhileShiftInProgressErrorShowsSubject = PublishSubject.create<Unit>()
    val cantStartNewShiftWhileShiftInProgressErrorShows: Observable<Unit> =
        cantStartNewShiftWhileShiftInProgressErrorShowsSubject.hide()

    init {
        updateShifts()
    }

    fun addShiftClicked() {
        if (shifts.firstOrNull { it.end.isNullOrBlank() } != null) {
            cantStartNewShiftWhileShiftInProgressErrorShowsSubject.onNext(Unit)
        } else {
            shiftStartConfirmsSubject.onNext(Unit)
        }
    }

    fun addShift() {
        buildShiftChangeFromCurrentLocationAndTime()
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

    fun endShift() {
        buildShiftChangeFromCurrentLocationAndTime()
            .doOnSubscribe { loadingVisibilityChangesSubject.onNext(true) }
            .doAfterTerminate { loadingVisibilityChangesSubject.onNext(false) }
            .flatMapCompletable { shiftsRepository.endShift(it) }
            .subscribe({
                Timber.d("Successfully ended shift")
                updateShifts()
            }, { error ->
                Timber.e(error, "Failed to end shift")
                error.message?.let { errorShowsSubject.onNext(it) }
            }).autoDispose(exits)
    }

    @SuppressLint("CheckResult")
    private fun buildShiftChangeFromCurrentLocationAndTime(): Single<ShiftChange> {
        return locationManager.getLocation()
            .map { location ->
                val currentDate = dateUtils.getCurrentDateString()
                ShiftChange(currentDate, location.latitude.toString(), location.longitude.toString())
            }
            .doOnError {
                if (it is SecurityException) {
                    requestLocationPermissionShowsSubject.onNext(Unit)
                }
            }
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