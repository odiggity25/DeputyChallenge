package com.orrie.deputychallenge.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orrie.deputychallenge.R
import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.views.ShiftHeaderView
import com.orrie.deputychallenge.views.ShiftView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ShiftsAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var adapterItems: List<AdapterItem> = listOf()

    private val shiftClicksSubject = PublishSubject.create<Shift>()
    val shiftClicks: Observable<Shift> = shiftClicksSubject.hide()

    fun updateShifts(newShifts: List<Shift>) {
        val shifts = newShifts.toMutableList()
        val adapterItems = mutableListOf<AdapterItem>()

        // If there is an ongoing shift add it along with a header to the top of the adapter items
        val currentShift = shifts.firstOrNull { it.end.isNullOrBlank() }
        if (currentShift != null) {
            adapterItems.add(AdapterItem.HeaderItem(context.getString(R.string.current_shift)))
            adapterItems.add(AdapterItem.ShiftItem(currentShift))
            shifts.remove(currentShift)
        }

        // Add the rest of the shifts to the adapter items
        adapterItems.add(AdapterItem.HeaderItem(context.getString(R.string.past_shifts)))
        adapterItems.addAll(shifts.map { AdapterItem.ShiftItem(it) })
        this.adapterItems = adapterItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AdapterItemType.Header.ordinal -> object: RecyclerView.ViewHolder(ShiftHeaderView(parent.context)) {}
            AdapterItemType.Shift.ordinal -> {
                val shiftView = ShiftView(parent.context)
                shiftView.clicks.subscribe { shiftClicksSubject.onNext(it) }
                object: RecyclerView.ViewHolder(shiftView) {}
            }
            else -> throw IllegalArgumentException("Unexpected view type")
        }
    }

    override fun getItemCount(): Int {
        return adapterItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adapterItem = adapterItems[position]
        when (adapterItem) {
            is AdapterItem.HeaderItem -> (holder.itemView as ShiftHeaderView).bind(adapterItem.text)
            is AdapterItem.ShiftItem -> (holder.itemView as ShiftView).bind(adapterItem.shift)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return adapterItems[position].adapterItemType.ordinal
    }

    enum class AdapterItemType {
        Header,
        Shift
    }

    sealed class AdapterItem(val adapterItemType: AdapterItemType) {
        data class HeaderItem(val text: String): AdapterItem(AdapterItemType.Header)
        data class ShiftItem(val shift: Shift): AdapterItem(AdapterItemType.Shift)
    }
}