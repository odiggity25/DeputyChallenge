package com.orrie.deputychallenge.apis

import com.orrie.deputychallenge.models.Shift
import io.reactivex.Single
import retrofit2.http.GET

interface ShiftsApi {

    @GET("/shifts")
    fun getShifts(): Single<List<Shift>>

}