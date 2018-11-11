package com.orrie.deputychallenge.apis

import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.models.ShiftChange
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ShiftsApi {

    @GET("shifts")
    fun getShifts(): Single<List<Shift>>

    @POST("shift/start")
    fun startShift(@Body shiftChange: ShiftChange): Completable

    @POST("shift/end")
    fun endShift(@Body shiftChange: ShiftChange): Completable

}