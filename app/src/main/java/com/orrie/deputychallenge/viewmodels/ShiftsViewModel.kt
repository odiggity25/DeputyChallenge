package com.orrie.deputychallenge.viewmodels

import android.annotation.SuppressLint
import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.models.ShiftChange
import com.orrie.deputychallenge.repositories.ShiftsRepository
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
    private val exits: Observable<Unit>
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

    private val locationErrorShowsSubject = PublishSubject.create<Unit>()
    val locationErrorShows: Observable<Unit> = locationErrorShowsSubject.hide()

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
            }, {
                Timber.e(it, "Failed to start shift")
                if (it is SecurityException) {
                    requestLocationPermissionShowsSubject.onNext(Unit)
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
            }, {
                Timber.e(it, "Failed to end shift")
            }).autoDispose(exits)
    }

    @SuppressLint("CheckResult")
    private fun buildShiftChangeFromCurrentLocationAndTime(): Single<ShiftChange> {
        return locationManager.getLocation()
            .map { location ->
                ShiftChange("May 10, 1988", location.latitude.toString(), location.longitude.toString())
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


        val shift1 = Shift(
            1,
            "May 3, 2018",
            "May 4, 2018",
            23.00F,
            24.00F,
            23.00F,
            24.00F,
            "https://unsplash.it/500/500/?random"
        )
        val shift2 = Shift(
            1,
            "May 5, 2018",
            "May 5, 2018",
            23.00F,
            24.00F,
            23.00F,
            24.00F,
            "https://unsplash.it/500/500/?random"
        )
        val shift3 = Shift(3, "May 5, 2018", "", 23.00F, 24.00F, 23.00F, 24.00F, "https://unsplash.it/500/500/?random")

        this.shifts = listOf(shift1, shift2, shift3)

        shiftsUpdatesSubject.onNext(shifts)
    }
}