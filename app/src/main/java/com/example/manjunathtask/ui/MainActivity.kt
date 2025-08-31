package com.example.manjunathtask.ui

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manjunathtask.R
import com.example.manjunathtask.data.api.RetrofitClient
import com.example.manjunathtask.data.repository.HoldingsRepository
import com.example.manjunathtask.databinding.ActivityMainBinding
import com.example.manjunathtask.ui.adapter.HoldingsAdapter
import com.example.manjunathtask.ui.viewmodel.HoldingsViewModel
import com.example.manjunathtask.ui.viewmodel.UiState
import com.example.manjunathtask.utils.Utility
import com.example.manjunathtask.utils.Utility.formatNumber
import com.example.manjunathtask.utils.Utility.formatWithPercentage
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: HoldingsAdapter
    private lateinit var viewModel: HoldingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAppBar()

        // ✅ Initialize ViewModel here
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val prefs = getSharedPreferences("app_cache", Context.MODE_PRIVATE)
                    val repo = HoldingsRepository(RetrofitClient.apiService, prefs, Gson())
                    return HoldingsViewModel(repo) as T
                }
            }
        )[HoldingsViewModel::class.java]

        adapter = HoldingsAdapter { /* expand collapse callback */ }
        binding.rvHoldings.layoutManager = LinearLayoutManager(this)
        binding.rvHoldings.adapter = adapter

        binding.swipeContainer.setOnRefreshListener {
            viewModel.refresh()
        }
        binding.summaryToggleRow.setOnClickListener {
            if (binding.expandedSection.visibility == View.VISIBLE) {
                binding.expandedSection.visibility = View.GONE
                binding.ivToggle.setImageResource(R.drawable.arrow_up)
            } else {
                binding.expandedSection.visibility = View.VISIBLE
                binding.ivToggle.setImageResource(R.drawable.arrow_down)
            }
        }

        observe()
    }

    private fun setupAppBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.account_circle)
        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty() && query.length > 2) {
                    performSearch(query)
                    hideKeyboard()
                    true
                } else {
                    false
                }
            } else false
        }

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.setText("")
            viewModel.refresh()
            toggleSearch(false)
        }
    }

    private fun performSearch(query: String) {
        adapter.filter(query)
    }

    private fun toggleSearch(show: Boolean) {
        if (show) {
            //supportActionBar?.hide()
            binding.toolbar.visibility = View.INVISIBLE
            binding.searchBar.visibility = View.VISIBLE
            binding.etSearch.requestFocus()
            showKeyboard()
        } else {
            //supportActionBar?.show()
            binding.toolbar.visibility = View.VISIBLE
            binding.searchBar.visibility = View.GONE
            adapter.filter("")
            hideKeyboard()
        }
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> binding.swipeContainer.isRefreshing = true
                    is UiState.Success -> {
                        binding.swipeContainer.isRefreshing = false
                        adapter.setData(state.holdings)
                        adapter.submitList(state.holdings)
                        if (state.holdings.isEmpty()) {
                            binding.tvNoHolding.visibility = View.VISIBLE
                            binding.summaryCard.visibility = View.GONE
                        } else {
                            binding.tvNoHolding.visibility = View.GONE
                            binding.summaryCard.visibility = View.VISIBLE
                        }
                        //binding.tvCurrentValue.text = "₹ ${state.summary.currentValue}"
                        binding.tvCurrentValue.text = "₹${formatNumber(state.summary.currentValue)}"
                        binding.tvTotalInvestmentValue.text = "₹${formatNumber(state.summary.totalInvestment)}"
                        binding.tvTotalpnlValue.text = formatWithPercentage("₹${formatNumber(state.summary.totalPNL)}", state.summary.totalPNLPercentage)
                        binding.tvTodayPnlValue.text = "₹${formatNumber(state.summary.todayPNL)}"
                        if (state.summary.todayPNL >= 0) {
                            binding.tvTodayPnlValue.setTextColor(resources.getColor(R.color.green))
                        } else {
                            binding.tvTodayPnlValue.setTextColor(resources.getColor(R.color.red))
                        }
                        if (state.summary.totalPNL >= 0) {
                            binding.tvTotalpnlValue.setTextColor(resources.getColor(R.color.green))
                        } else {
                            binding.tvTotalpnlValue.setTextColor(resources.getColor(R.color.red))
                        }
                    }
                    is UiState.Error -> {
                        binding.swipeContainer.isRefreshing = false
                        binding.tvNoHolding.visibility = View.VISIBLE
                        binding.summaryCard.visibility = View.GONE
                        // show error with Toast/Snackbar
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.portfolio, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                toggleSearch(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

