package com.orrie.deputychallenge.viewmodels

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.models.ShiftChange
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.ImmediateSchedulersRule
import com.orrie.deputychallenge.utils.ShiftUtils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Rule
import org.junit.Test

class ShiftsViewModelTest {

    @get:Rule
    val immediateSchedulersRule = ImmediateSchedulersRule()

    private lateinit var shiftsViewModelUnderTest: ShiftsViewModel
    private lateinit var loadingChangesObserver: TestObserver<Boolean>
    private lateinit var shiftsUpdatesObserver: TestObserver<List<Shift>>
    private lateinit var shiftInProgressErrorShowsObserver: TestObserver<Unit>
    private lateinit var shiftStartConfirmsObserver: TestObserver<Unit>
    private lateinit var shiftEndConfirmsObserver: TestObserver<Unit>
    private lateinit var errorShowsObserver: TestObserver<String>
    private lateinit var requestLocationPermissionShowsObserver: TestObserver<Unit>
    private val mockShiftsRepository: ShiftsRepository = mock()
    private val mockExits: Observable<Unit> = PublishSubject.create()
    private val mockShiftUtils: ShiftUtils = mock()


    // These variables are all defined at the top to make reading the tests much simpler
    private val emptyListOfShifts = listOf<Shift>()
    private val newShiftChange = ShiftChange("", "", "")
    private val endedCurrentShiftChange = ShiftChange("", "", "")
    private val currentShift = Shift(1, "may 1", "", 1.0F, 1.0F, null, null, "image")
    private val currentShiftEnded = Shift(1, "may 1", "may 2", 1.0F, 1.0F, 1F, 1F, "image")
    private val listOfShiftsWithJustTheCurrentShift = listOf(currentShift)
    private val oldShift1 = Shift(3, "may 1", "may 2", 1.0F, 1.0F, 1F, 1F, "image")
    private val oldShift2 = Shift(4, "may 1", "may 2", 1.0F, 1.0F, 1.0F, 1F, "image")
    private val oldShifts = listOf(oldShift1, oldShift2)
    private val oldShiftsWithCurrentShift = listOf(currentShift, oldShift1, oldShift2)
    private val oldShiftsWithCurrentShiftEnded = listOf(currentShiftEnded, oldShift1, oldShift2)
    private val securityException = SecurityException()
    private val nullLocationExceptionMessage = "null location"
    private val nullLocationException = Exception(nullLocationExceptionMessage)

    @Test
    fun `add shift button clicked with no shift in progress`() {
        // All tests are in the Given, When, Then format separated by an empty line
        // Given
        whenever(mockShiftsRepository.getShifts()).thenReturn(Single.just(emptyListOfShifts))
        setupViewModel()

        // When
        shiftsViewModelUnderTest.addShiftClicked()

        // Then
        shiftStartConfirmsObserver.assertValueCount(1)
    }

    @Test
    fun `add shift button clicked with shift in progress`() {
        whenever(mockShiftsRepository.getShifts()).thenReturn(Single.just(listOfShiftsWithJustTheCurrentShift))
        setupViewModel()

        shiftsViewModelUnderTest.addShiftClicked()

        shiftInProgressErrorShowsObserver.assertValueCount(1)
    }

    @Test
    fun `confirm add shift clicked with no existing shifts`() {
        whenever(mockShiftsRepository.getShifts()).thenReturn(
            Single.just(emptyListOfShifts),
            Single.just(listOfShiftsWithJustTheCurrentShift)
        )
        setupViewModel()
        whenever(mockShiftUtils.buildShiftChangeFromCurrentLocationAndTime()).thenReturn(Single.just(newShiftChange))
        whenever(mockShiftsRepository.startShift(newShiftChange)).thenReturn(Completable.complete())

        shiftsViewModelUnderTest.confirmAddShiftClicked()

        shiftsUpdatesObserver.assertValues(emptyListOfShifts, listOfShiftsWithJustTheCurrentShift)
        assertLoadingShowsAndHides()
    }

    @Test
    fun `confirm add shift clicked when past shifts exist`() {
        whenever(mockShiftsRepository.getShifts()).thenReturn(
            Single.just(oldShifts),
            Single.just(oldShiftsWithCurrentShift)
        )
        setupViewModel()
        whenever(mockShiftUtils.buildShiftChangeFromCurrentLocationAndTime()).thenReturn(Single.just(newShiftChange))
        whenever(mockShiftsRepository.startShift(newShiftChange)).thenReturn(Completable.complete())

        shiftsViewModelUnderTest.confirmAddShiftClicked()

        shiftsUpdatesObserver.assertValues(oldShifts, oldShiftsWithCurrentShift)
        assertLoadingShowsAndHides()
    }

    @Test
    fun `confirm add shift clicked without location permission`() {
        whenever(mockShiftsRepository.getShifts()).thenReturn(Single.just(emptyListOfShifts))
        whenever(mockShiftUtils.buildShiftChangeFromCurrentLocationAndTime()).thenReturn(Single.error(securityException))
        setupViewModel()

        shiftsViewModelUnderTest.confirmAddShiftClicked()

        requestLocationPermissionShowsObserver.assertValueCount(1)
    }

    @Test
    fun `confirm add shift clicked and exception thrown`() {
        whenever(mockShiftsRepository.getShifts()).thenReturn(Single.just(emptyListOfShifts))
        whenever(mockShiftUtils.buildShiftChangeFromCurrentLocationAndTime()).thenReturn(
            Single.error(
                nullLocationException
            )
        )
        setupViewModel()

        shiftsViewModelUnderTest.confirmAddShiftClicked()

        errorShowsObserver.assertValue(nullLocationException.message)
    }

    @Test
    fun `confirm end shift clicked when past shifts exist`() {
        whenever(mockShiftsRepository.getShifts()).thenReturn(
            Single.just(oldShiftsWithCurrentShift),
            Single.just(oldShiftsWithCurrentShiftEnded)
        )
        setupViewModel()
        whenever(mockShiftUtils.buildShiftChangeFromCurrentLocationAndTime()).thenReturn(Single.just(endedCurrentShiftChange))
        whenever(mockShiftsRepository.endShift(endedCurrentShiftChange)).thenReturn(Completable.complete())

        shiftsViewModelUnderTest.confirmEndShiftClicked()

        shiftsUpdatesObserver.assertValues(oldShiftsWithCurrentShift, oldShiftsWithCurrentShiftEnded)
        assertLoadingShowsAndHides()
    }

    @Test
    fun `confirm end shift clicked without location permission`() {
        // This is unlikely to happen but could happen if you start a shift on one device and finish it on another
        whenever(mockShiftsRepository.getShifts()).thenReturn(Single.just(emptyListOfShifts))
        whenever(mockShiftUtils.buildShiftChangeFromCurrentLocationAndTime()).thenReturn(Single.error(securityException))
        setupViewModel()

        shiftsViewModelUnderTest.confirmEndShiftClicked()

        requestLocationPermissionShowsObserver.assertValueCount(1)
    }

    @Test
    fun `confirm end shift clicked and exception thrown`() {
        whenever(mockShiftsRepository.getShifts()).thenReturn(Single.just(emptyListOfShifts))
        whenever(mockShiftUtils.buildShiftChangeFromCurrentLocationAndTime()).thenReturn(
            Single.error(
                nullLocationException
            )
        )
        setupViewModel()

        shiftsViewModelUnderTest.confirmEndShiftClicked()

        errorShowsObserver.assertValue(nullLocationException.message)
    }


    /**
     * Because the loading spinner may show and hide several times (e.g. performing
     * an api call and then refreshing the data) this is a helper method to ensure it
     * shows at least once and finishes hidden
     */
    private fun assertLoadingShowsAndHides() {
        val loadingValues = loadingChangesObserver.values()
        assert(loadingValues.contains(true))
        assert(loadingValues.last() == false)
    }

    private fun setupViewModel() {
        shiftsViewModelUnderTest = ShiftsViewModel(
            mockShiftsRepository,
            mockExits,
            mockShiftUtils
        )
        loadingChangesObserver = shiftsViewModelUnderTest.loadVisibilityChanges.test()
        shiftsUpdatesObserver = shiftsViewModelUnderTest.shiftsUpdates.test()
        shiftInProgressErrorShowsObserver = shiftsViewModelUnderTest.shiftInProgressErrorShows.test()
        shiftStartConfirmsObserver = shiftsViewModelUnderTest.shiftStartConfirms.test()
        shiftEndConfirmsObserver = shiftsViewModelUnderTest.shiftEndConfirms.test()
        errorShowsObserver = shiftsViewModelUnderTest.errorShows.test()
        requestLocationPermissionShowsObserver = shiftsViewModelUnderTest.requestLocationPermissionShows.test()
    }
}