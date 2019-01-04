package com.example.robin.smartplug;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.macroyau.thingspeakandroid.ThingSpeakLineChart;

import java.util.Calendar;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class HistoryView extends PageView {

    private ThingSpeakLineChart tempTSChart, humiTSChart, powerTSChart;
    private LineChartView tempChartView, humiChartView, powerChartView;
    public Calendar calendar;
    public View view;

    public HistoryView(Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.page_histroy, null);
        // Create a Calendar object dated 5 minutes ago
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);

        tempChart();
        humiChart();
        powerChart();
        addView(view);
    }

    public void tempChart(){
        // Configure LineChartView
        tempChartView = view.findViewById(R.id.temp_chart);
        tempChartView.setZoomEnabled(false);
        tempChartView.setValueSelectionEnabled(true);

        // Create a line chart from Field1 of ThinkSpeak Channel
        tempTSChart = new ThingSpeakLineChart(MainActivity.getCHANNELID(), 1, MainActivity.getREADAPIKEY());
        // Get 10 entries at maximum
        tempTSChart.setNumberOfEntries(10);
        // Set value axis labels on 10-unit interval
        tempTSChart.setValueAxisLabelInterval(10);
        // Set date axis labels on 5-minute interval
        tempTSChart.setDateAxisLabelInterval(1);
        // Show the line as a cubic spline
        tempTSChart.useSpline(true);
        // Set the line color
        tempTSChart.setLineColor(Color.parseColor("#D32F2F"));
        // Set the axis color
        tempTSChart.setAxisColor(Color.parseColor("#455a64"));
        // Set the starting date (5 minutes ago) for the default viewport of the chart
        tempTSChart.setChartStartDate(calendar.getTime());
        // Set listener for chart data update
        tempTSChart.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
            @Override
            public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                // Set chart data to the LineChartView
                tempChartView.setLineChartData(lineChartData);
                // Set scrolling bounds of the chart
                tempChartView.setMaximumViewport(maxViewport);
                // Set the initial chart bounds
                tempChartView.setCurrentViewport(initialViewport);
            }
        });
        // Load chart data asynchronously
        tempTSChart.loadChartData();
    }

    private void humiChart() {
        // Configure LineChartView
        humiChartView = view.findViewById(R.id.humi_chart);
        humiChartView.setZoomEnabled(false);
        humiChartView.setValueSelectionEnabled(true);

        // Create a line chart from Field2 of ThinkSpeak Channel
        humiTSChart = new ThingSpeakLineChart(MainActivity.getCHANNELID(), 2, MainActivity.getREADAPIKEY());
        // Get 10 entries at maximum
        humiTSChart.setNumberOfEntries(10);
        // Set value axis labels on 10-unit interval
        humiTSChart.setValueAxisLabelInterval(10);
        // Set date axis labels on 5-minute interval
        humiTSChart.setDateAxisLabelInterval(1);
        // Show the line as a cubic spline
        humiTSChart.useSpline(true);
        // Set the line color
        humiTSChart.setLineColor(Color.parseColor("#D32F2F"));
        // Set the axis color
        humiTSChart.setAxisColor(Color.parseColor("#455a64"));
        // Set the starting date (5 minutes ago) for the default viewport of the chart
        humiTSChart.setChartStartDate(calendar.getTime());
        // Set listener for chart data update
        humiTSChart.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
            @Override
            public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                // Set chart data to the LineChartView
                humiChartView.setLineChartData(lineChartData);
                // Set scrolling bounds of the chart
                humiChartView.setMaximumViewport(maxViewport);
                // Set the initial chart bounds
                humiChartView.setCurrentViewport(initialViewport);
            }
        });
        // Load chart data asynchronously
        humiTSChart.loadChartData();
    }

    private void powerChart() {
        // Configure LineChartView
        powerChartView = view.findViewById(R.id.power_chart);
        powerChartView.setZoomEnabled(false);
        powerChartView.setValueSelectionEnabled(true);

        // Create a line chart from Field3 of ThinkSpeak Channel
        powerTSChart = new ThingSpeakLineChart(MainActivity.getCHANNELID(), 3, MainActivity.getREADAPIKEY());
        // Get 10 entries at maximum
        powerTSChart.setNumberOfEntries(10);
        // Set value axis labels on 10-unit interval
        powerTSChart.setValueAxisLabelInterval(10);
        // Set date axis labels on 5-minute interval
        powerTSChart.setDateAxisLabelInterval(1);
        // Show the line as a cubic spline
        powerTSChart.useSpline(true);
        // Set the line color
        powerTSChart.setLineColor(Color.parseColor("#D32F2F"));
        // Set the axis color
        powerTSChart.setAxisColor(Color.parseColor("#455a64"));
        // Set the starting date (5 minutes ago) for the default viewport of the chart
        powerTSChart.setChartStartDate(calendar.getTime());
        // Set listener for chart data update
        powerTSChart.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
            @Override
            public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                // Set chart data to the LineChartView
                powerChartView.setLineChartData(lineChartData);
                // Set scrolling bounds of the chart
                powerChartView.setMaximumViewport(maxViewport);
                // Set the initial chart bounds
                powerChartView.setCurrentViewport(initialViewport);
            }
        });
        // Load chart data asynchronously
        powerTSChart.loadChartData();
    }

    @Override
    public void refreshView() {

    }
}
