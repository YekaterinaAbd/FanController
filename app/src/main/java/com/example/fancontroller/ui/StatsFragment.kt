package com.example.fancontroller.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.fancontroller.R
import com.example.fancontroller.databinding.FragmentStatsBinding
import com.example.fancontroller.model.Humidity
import com.example.fancontroller.model.Temperature
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.floor

class StatsFragment : Fragment(R.layout.fragment_stats) {

    private val binding by viewBinding(FragmentStatsBinding::bind)
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = requireContext().getLightPurple()
        bindViews()
        observeViewModel()
        viewModel.getTemperatureList()
        viewModel.getHumidityList()
    }

    private fun bindViews() = with(binding) {
        tempChart.bind()
        humChart.bind()
    }

    private fun BarChart.bind() {
        this.apply {
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)

            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f

            legend.isEnabled = false
            description.isEnabled = false

            animateY(2000)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f

            axisRight.setDrawLabels(false)

            axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return (floor(value.toDouble()).toInt()).toString()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.temperatureLD.observe(viewLifecycleOwner) {
            setTemperatureList(it)
        }
        viewModel.humidityLD.observe(viewLifecycleOwner) {
            setHumidityList(it)
        }
    }

    private fun setTemperatureList(temperature: List<Temperature>) = with(binding) {
        val values = mutableListOf<BarEntry>()
        val dates = mutableListOf<String>()

        for (i in temperature.indices) {
            values.add(BarEntry(i.toFloat(), temperature[i].temp.toFloat()))
            dates.add(temperature[i].date)
        }

        val xAxis = binding.tempChart.xAxis
        xAxis.valueFormatter = MyXAxisFormatter(dates)

        val barDataSet = BarDataSet(values, "Temperature, C")
        val data = BarData(barDataSet)
        data.setValueTextSize(12f)
        tempChart.data = data

        tempChart.axisLeft.granularity = 1.0f
        tempChart.axisLeft.isGranularityEnabled = true

        tempChart.invalidate()
    }

    private fun setHumidityList(humidity: List<Humidity>) = with(binding) {
        val values = mutableListOf<BarEntry>()
        val dates = mutableListOf<String>()

        for (i in humidity.indices) {
            values.add(BarEntry(i.toFloat(), humidity[i].hum.toFloat()))
            dates.add(humidity[i].date)
        }

        val xAxis = binding.humChart.xAxis
        xAxis.valueFormatter = MyXAxisFormatter(dates)

        val barDataSet = BarDataSet(values, "Humidity, %")
        val data = BarData(barDataSet)
        data.setValueTextSize(12f)
        humChart.data = data

        humChart.axisLeft.granularity = 1.0f
        humChart.axisLeft.isGranularityEnabled = true

        humChart.invalidate()
    }

    inner class MyXAxisFormatter(private val list: List<String>) : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            // Log.d(TAG, "getAxisLabel: index $index")
            return if (index < list.size) {
                list[index]
            } else {
                ""
            }
        }
    }

}