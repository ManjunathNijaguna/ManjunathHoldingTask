package com.example.manjunathtask.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manjunathtask.R
import com.example.manjunathtask.data.model.Holding
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.example.manjunathtask.utils.Utility
import com.example.manjunathtask.utils.Utility.buildStyledText
import com.example.manjunathtask.utils.Utility.formatNumber
import java.text.NumberFormat
import java.util.Locale

class HoldingsAdapter(
    private val onExpandToggle: (String) -> Unit
) : ListAdapter<Holding, HoldingsAdapter.HoldingVH>(DiffCallback()) {

    private val expandedSet = mutableSetOf<String>()
    private var fullList: List<Holding> = emptyList()

    fun setData(newList: List<Holding>) {
        fullList = newList
        submitList(newList)
    }

    fun filter(query: String) {
        if (query.isBlank()) {
            submitList(fullList)
        } else {
            val filtered = fullList.filter {
                it.symbol.contains(query, ignoreCase = true)
            }
            submitList(filtered)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_holding, parent, false)
        return HoldingVH(v)
    }

    override fun onBindViewHolder(holder: HoldingVH, position: Int) {
        val item = getItem(position)
        holder.bind(item, expandedSet.contains(item.symbol))
        holder.itemView.setOnClickListener {
            if (expandedSet.contains(item.symbol)) expandedSet.remove(item.symbol) else {
                expandedSet.add(item.symbol)
            }
            notifyItemChanged(position)
            onExpandToggle(item.symbol)
        }
    }

    class HoldingVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvSymbol: TextView = view.findViewById(R.id.tvSymbol)
        val tvLtp: TextView = view.findViewById(R.id.tvLtp)
        val tvQuantity: TextView = view.findViewById(R.id.tvAvgQty)
        val tvProfitLoss: TextView = view.findViewById(R.id.tvInvested)
        val expandedLayout: View = view.findViewById(R.id.expandedLayout)

        fun bind(h: Holding, expanded: Boolean) {
            tvSymbol.text = h.symbol
            tvLtp.text = buildStyledText("LTP", "₹ ${formatNumber(h.ltp)}", Color.DKGRAY, false)
            tvQuantity.text = buildStyledText("NET QTY", h.quantity.toString(), Color.BLACK, true)
            tvProfitLoss.text = buildStyledText("P&L", "₹${formatNumber(h.pnl)}",
                if (h.pnl >= 0) Color.parseColor("#388E3C") else Color.parseColor("#D32F2F"),
                false
            )
            expandedLayout.visibility = if (expanded) View.VISIBLE else View.GONE
            // fill expanded fields
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Holding>() {
        override fun areItemsTheSame(oldItem: Holding, newItem: Holding): Boolean = oldItem.symbol == newItem.symbol
        override fun areContentsTheSame(oldItem: Holding, newItem: Holding): Boolean = oldItem == newItem
    }
}
