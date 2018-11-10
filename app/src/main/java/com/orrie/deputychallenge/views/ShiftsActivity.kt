package com.orrie.deputychallenge.views

import android.os.Bundle
import com.orrie.deputychallenge.DeputyChallengeApplication
import com.orrie.deputychallenge.R
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.viewmodels.ShiftsViewModel
import javax.inject.Inject

class ShiftsActivity : BaseActivity() {

    @Inject
    lateinit var shiftsRepository: ShiftsRepository

    lateinit var shiftsViewModel: ShiftsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shifts)
        DeputyChallengeApplication.appComponent.inject(this)

        initUi()
    }

    private fun initUi() {
        shiftsViewModel = ShiftsViewModel(shiftsRepository, exits)
    }
}
