package com.orrie.deputychallenge.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.orrie.deputychallenge.R
import kotlinx.android.synthetic.main.item_shift_header.view.*

class ShiftHeaderView(context: Context): ConstraintLayout(context) {

    init {
        layoutParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        View.inflate(context, R.layout.item_shift_header, this)
        setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
    }

    fun bind(headerText: String) {
        headerTextView.text = headerText
    }
}