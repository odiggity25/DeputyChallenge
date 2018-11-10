package com.orrie.deputychallenge.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.orrie.deputychallenge.R
import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.utils.throttleClicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_shift.view.*

class ShiftView(context: Context): ConstraintLayout(context) {

    private var shift: Shift? = null
    val clicks: Observable<Shift> = throttleClicks()
        .filter { shift != null }
        .map { shift }

    init {
        layoutParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        View.inflate(context, R.layout.item_shift, this)
    }

    fun bind(shift: Shift) {
        this.shift = shift
        shiftStartTime.text = context.getString(R.string.start_time, shift.start)
        shiftEndTime.text = context.getString(R.string.end_time, shift.end)
        Glide.with(context)
            .load(shift.image)
            .into(shiftImageView)
    }
}