package com.orrie.deputychallenge.views

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orrie.deputychallenge.DeputyChallengeApplication
import com.orrie.deputychallenge.R
import com.orrie.deputychallenge.adapters.ShiftsAdapter
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.LocationManager
import com.orrie.deputychallenge.utils.dpToPx
import com.orrie.deputychallenge.utils.subscribeAndObserveOnMainThread
import com.orrie.deputychallenge.viewmodels.ShiftsViewModel
import kotlinx.android.synthetic.main.activity_shifts.*
import javax.inject.Inject

class ShiftsActivity : BaseActivity() {

    @Inject
    lateinit var shiftsRepository: ShiftsRepository

    @Inject
    lateinit var locationManager: LocationManager

    lateinit var shiftsViewModel: ShiftsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shifts)
        DeputyChallengeApplication.appComponent.inject(this)

        initUi()
    }

    private val REQUEST_PERMISSION_LOCATION = 1221

    private fun initUi() {
        setSupportActionBar(shiftsToolbar)
        supportActionBar?.title = "Deputy Challenge"


        shiftsViewModel = ShiftsViewModel(shiftsRepository, locationManager, exits)

        val shiftsAdapter = ShiftsAdapter(this)
        shiftsRecyclerView.adapter = shiftsAdapter
        shiftsRecyclerView.layoutManager = LinearLayoutManager(this)
        shiftsRecyclerView.addItemDecoration(ItemSpacer())

        shiftsViewModel.shiftsUpdates.subscribeAndObserveOnMainThread { shiftsAdapter.updateShifts(it) }
        shiftsViewModel.requestLocationPermissionShows.subscribeAndObserveOnMainThread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_LOCATION)
            }
        }
    }

    private class ItemSpacer : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = 6.dpToPx(view.context)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.shifts_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addShiftButton -> {
                shiftsViewModel.addShiftClicked()
                true
            }
            else -> false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_PERMISSION_LOCATION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shiftsViewModel.addShiftClicked()
                }
            }
        }
}
