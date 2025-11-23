package com.example.myapplication.ui.activity.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.FiveWeatherForecast;
import com.example.myapplication.data.respository.FloodDataAPIHandler;
import com.example.myapplication.ui.adapter.HourlyForecastAdapter;
import com.example.myapplication.utils.GlobalUtility;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class WeatherMonitorActivity extends AppCompatActivity {

    private TextView tvTemperature, tvCondition, tvDate;
    private TextView tvHumidity, tvWind, tvRainfall;
    private TextView tvTotalRainfall, tvFloodStatus;
    private View alertBanner;
    private FloodDataAPIHandler apiHandler;
    private Activity activity;
    private Context context;
    private RecyclerView rvHourlyForecast;
    private com.example.myapplication.ui.charts.RainfallChartView rainfallChart;

    private HourlyForecastAdapter adapter;
    private List<FiveWeatherForecast.HourlyWeatherForecast> hourlyData = new ArrayList<>();
    private Handler handler = new Handler();

    private GlobalUtility globalUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_monitor);

        initViews();
        setupHourlyData();
    }

    private void initViews() {
        activity = this;
        context = this;
        tvTemperature = findViewById(R.id.tv_temperature);
        tvCondition = findViewById(R.id.tv_condition);
        tvDate = findViewById(R.id.tv_date);
        tvHumidity = findViewById(R.id.tv_humidity);
        tvWind = findViewById(R.id.tv_wind);
        tvRainfall = findViewById(R.id.tv_rainfall);
        tvTotalRainfall = findViewById(R.id.tv_total_rainfall);
        tvFloodStatus = findViewById(R.id.tv_flood_status);
        alertBanner = findViewById(R.id.alert_banner);
        rvHourlyForecast = findViewById(R.id.rv_hourly_forecast);
        rainfallChart = findViewById(R.id.rainfall_chart);
        apiHandler = new FloodDataAPIHandler(activity, context);
    }

    private void setupHourlyData() {
        apiHandler.getFiveHoursWeatherForecast(new ResponseCallback<FiveWeatherForecast>() {
            @Override
            public void onSuccess(FiveWeatherForecast response) {
                hourlyData.clear(); //clear first then add the new one
                hourlyData = response.getData();

                if(!hourlyData.isEmpty()){

                    runOnUiThread(() -> {
                        if(adapter == null){
                            setupRecyclerView();

                        }else{
                            adapter.updateData(hourlyData);

                        }
                        updateUI();
                    });


                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WEATHER_MONITOR_ACTIVITY", Objects.requireNonNull(t.getMessage()));
            }
        });


    }

    private void setupRecyclerView() {
        adapter = new HourlyForecastAdapter(hourlyData);
        rvHourlyForecast.setLayoutManager(new LinearLayoutManager(this));
        rvHourlyForecast.setAdapter(adapter);
    }

    private void updateUI() {


            //get the list of data
            FiveWeatherForecast.HourlyWeatherForecast weather = hourlyData.get(0);

            //Initial data
            double temperature = weather.getTemperature();
            double precipitation = weather.getPrecipitation();
            int humidity = weather.getHumidity();
            double windSpeed = weather.getWind_speed();

            // Update temperature
            String condition = getRainStatus(precipitation);
            tvTemperature.setText(String.format(Locale.getDefault(), "%.0f°", temperature));
            tvCondition.setText(condition);

            // Update date

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
            tvDate.setText(dateFormat.format(new Date()));

            // Update stats

            tvHumidity.setText(String.format(Locale.getDefault(), "%d%%", humidity));
            tvWind.setText(String.format(Locale.getDefault(), "%.0fkm/hr", windSpeed));
            tvRainfall.setText(String.format(Locale.getDefault(), "%.1fmm", precipitation));

            // Update alert banner
            if (precipitation > 2.0) {
                alertBanner.setVisibility(View.VISIBLE);
            } else {
                alertBanner.setVisibility(View.GONE);
            }


//             Update chart - FIXED HERE
        if (rainfallChart != null) {
            float[] rainfallValues = new float[hourlyData.size()];
            for (int i = 0; i < hourlyData.size(); i++) {
                rainfallValues[i] = (float) hourlyData.get(i).getPrecipitation();
            }
            rainfallChart.setData(rainfallValues);
        }

//             Update total rainfall
        double totalRain = 0;
        for (FiveWeatherForecast.HourlyWeatherForecast hourlyWeather : hourlyData) {
            totalRain += weather.getPrecipitation();
        }
        tvTotalRainfall.setText(String.format(Locale.getDefault(),
                "Total: %.1fmm over 5 hours", totalRain));

            // Update flood status
            String status = getRainStatus(precipitation);
            tvFloodStatus.setText(status);

            if (precipitation > 2.0) {
                tvFloodStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else if (precipitation > 1.0) {
                tvFloodStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                tvFloodStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            }


    }

    private String getRainStatus(double rainfall) {
        if (rainfall > 2.0) return "Heavy Rainfall - Monitoring";
        if (rainfall > 1.0) return "Moderate Rainfall - Normal";
        return "Light Rainfall - Normal";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupHourlyData();
    }
}