package com.orrie.deputychallenge.views

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orrie.deputychallenge.DeputyChallengeApplication
import com.orrie.deputychallenge.R
import com.orrie.deputychallenge.adapters.ShiftsAdapter
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.dpToPx
import com.orrie.deputychallenge.utils.subscribeAndObserveOnMainThread
import com.orrie.deputychallenge.viewmodels.ShiftsViewModel
import kotlinx.android.synthetic.main.activity_shifts.*
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

        val shiftsAdapter = ShiftsAdapter(this)
        shiftsRecyclerView.adapter = shiftsAdapter
        shiftsRecyclerView.layoutManager = LinearLayoutManager(this)
        shiftsRecyclerView.addItemDecoration(ItemSpacer())

        shiftsViewModel.shiftsUpdates.subscribeAndObserveOnMainThread { shiftsAdapter.updateShifts(it) }
    }

    private class ItemSpacer : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = 6.dpToPx(view.context)
        }
    }
}
