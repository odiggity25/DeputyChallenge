package com.orrie.deputychallenge.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orrie.deputychallenge.DeputyChallengeApplication
import com.orrie.deputychallenge.R
import com.orrie.deputychallenge.apis.ShiftsApi
import com.orrie.deputychallenge.repositories.ShiftsRepository
import javax.inject.Inject

class ShiftsActivity : AppCompatActivity() {

    @Inject
    lateinit var shiftsRepository: ShiftsRepository

    @Inject
    lateinit var shiftsApi: ShiftsApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shifts)
        DeputyChallengeApplication.appComponent.inject(this)
    }
}
