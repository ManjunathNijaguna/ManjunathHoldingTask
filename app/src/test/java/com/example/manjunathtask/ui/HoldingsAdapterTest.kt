package com.example.manjunathtask.ui

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.manjunathtask.R
import com.example.manjunathtask.domain.model.Holding
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import androidx.test.core.app.ApplicationProvider
import com.example.manjunathtask.ui.adapter.HoldingsAdapter
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [34],
    application = Application::class,
    packageName = "com.example.manjunathtask"
)
class HoldingsAdapterTest {

    private lateinit var adapter: HoldingsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var context: Context

    private val sampleHoldings = listOf(
        Holding(symbol = "TCS", ltp = 3500.5, quantity = 10, close = 5000.0),
        Holding(symbol = "INFY", ltp = 1550.0, quantity = 20, close = -2000.0),
        Holding(symbol = "HDFC", ltp = 2800.0, quantity = 15, close = 1000.0)
    )

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.setTheme(R.style.Theme_ManjunathTask)

        recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = HoldingsAdapter(onExpandToggle = {})
        recyclerView.adapter = adapter
    }

    @Test
    fun `setData should update list`() {
        adapter.setData(sampleHoldings)
        assertEquals(3, adapter.itemCount)
    }

    @Test
    fun `bind should set text correctly`() {
        adapter.setData(sampleHoldings)
        val holder = createViewHolder()
        adapter.onBindViewHolder(holder, 0)
        val tvSymbol = holder.itemView.findViewById<TextView>(R.id.tvSymbol)
        assertEquals("TCS", tvSymbol.text.toString())
    }

    @Test
    fun `click should toggle expansion and invoke callback`() {
        val callback = mockk<(String) -> Unit>(relaxed = true)
        adapter = HoldingsAdapter(onExpandToggle = callback)
        adapter.setData(sampleHoldings)

        val holder = createViewHolder()
        adapter.onBindViewHolder(holder, 0)

        assertEquals(android.view.View.GONE, holder.expandedLayout.visibility)

        holder.itemView.performClick()
        adapter.onBindViewHolder(holder, 0)
        assertEquals(android.view.View.VISIBLE, holder.expandedLayout.visibility)
        verify { callback("TCS") }

        holder.itemView.performClick()
        adapter.onBindViewHolder(holder, 0)
        assertEquals(android.view.View.GONE, holder.expandedLayout.visibility)
    }

    private fun createViewHolder(): HoldingsAdapter.HoldingVH {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_holding, recyclerView, false)
        return HoldingsAdapter.HoldingVH(view)
    }
}
