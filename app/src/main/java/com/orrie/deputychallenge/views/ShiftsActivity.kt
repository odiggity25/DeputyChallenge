package com.orrie.deputychallenge.views

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orrie.deputychallenge.DeputyChallengeApplication
import com.orrie.deputychallenge.R
import com.orrie.deputychallenge.adapters.ShiftsAdapter
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.ShiftUtils
import com.orrie.deputychallenge.utils.dpToPx
import com.orrie.deputychallenge.utils.subscribeAndObserveOnMainThread
import com.orrie.deputychallenge.viewmodels.ShiftsViewModel
import kotlinx.android.synthetic.main.activity_shifts.*
import javax.inject.Inject

private const val REQUEST_PERMISSION_LOCATION = 1221

class ShiftsActivity : BaseActivity() {

    @Inject
    lateinit var shiftsRepository: ShiftsRepository

    @Inject
    lateinit var shiftUtils: ShiftUtils

    lateinit var shiftsViewModel: ShiftsViewModel

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shifts)
        DeputyChallengeApplication.appComponent.inject(this)

        initUi()
    }

    private fun initUi() {
        setSupportActionBar(shiftsToolbar)
        supportActionBar?.title = "Deputy Challenge"

        shiftsViewModel = ShiftsViewModel(shiftsRepository, exits, shiftUtils)

        val shiftsAdapter = ShiftsAdapter(this)
        shiftsRecyclerView.adapter = shiftsAdapter
        shiftsRecyclerView.layoutManager = LinearLayoutManager(this)
        shiftsRecyclerView.addItemDecoration(ItemSpacer())

        shiftsAdapter.shiftClicks.subscribeAndObserveOnMainThread { shiftsViewModel.shiftClicked(it) }

        shiftsViewModel.shiftsUpdates.subscribeAndObserveOnMainThread { shiftsAdapter.updateShifts(it) }
        shiftsViewModel.requestLocationPermissionShows.subscribeAndObserveOnMainThread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_LOCATION)
            }
        }
        shiftsViewModel.loadVisibilityChanges.subscribeAndObserveOnMainThread {
            shiftsProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        shiftsViewModel.shiftInProgressErrorShows.subscribeAndObserveOnMainThread { showCantStartNewShiftDialog() }
        shiftsViewModel.shiftStartConfirms.subscribeAndObserveOnMainThread { showNewShiftConfirmationDialog() }
        shiftsViewModel.shiftEndConfirms.subscribeAndObserveOnMainThread { showEndShiftConfirmationDialog() }
        shiftsViewModel.errorShows.subscribeAndObserveOnMainThread { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
    }

    private fun showCantStartNewShiftDialog() {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.shift_in_progress))
            .setMessage(getString(R.string.shift_in_progress_explanation))
            .setPositiveButton(getString(R.string.ok), null)
            .create()
        alertDialog?.show()
    }

    private fun showNewShiftConfirmationDialog() {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.start_shift))
            .setMessage(getString(R.string.start_shift_explanation))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> shiftsViewModel.confirmAddShiftClicked() }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        alertDialog?.show()
    }

    private fun showEndShiftConfirmationDialog() {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.end_shift))
            .setMessage(getString(R.string.end_shift_explanation))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> shiftsViewModel.confirmEndShiftClicked() }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        alertDialog?.show()
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
