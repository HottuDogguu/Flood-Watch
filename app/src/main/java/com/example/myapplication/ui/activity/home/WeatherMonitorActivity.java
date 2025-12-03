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
import com.example.myapplication.data.models.api_response.ApiMeteoResponse;
import com.example.myapplication.data.models.api_response.FiveWeatherForecast;
import com.example.myapplication.data.respository.FloodDataAPIHandler;
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
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
    private UsersAPIRequestHandler usersAPIRequestHandler;
    List<FiveWeatherForecast.HourlyWeatherForecast> hourlyNewData = new ArrayList<>();


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
        usersAPIRequestHandler = new UsersAPIRequestHandler(activity, context);
    }

    private void setupHourlyData() {
        usersAPIRequestHandler.getInitialForecastData(new ResponseCallback<ApiMeteoResponse>() {
            @Override
            public void onSuccess(ApiMeteoResponse response) {

                hourlyNewData = new ArrayList<>();

                List<Integer> precipitation_probability = response.getData().getPrecipitation_probability();
                List<String> hourly_time = response.getData().getHourly_time();
                List<Integer> temperatures = response.getData().getTemperatures();

                List<Double> precipitation = response.getData().getPrecipitation();
                List<Double> wind_speed = response.getData().getWind_speed();
                List<Integer> humidity = response.getData().getHumidity();

                hourlyNewData.clear();
                for (int i = 0; i < precipitation.size(); i++) {
                    hourlyNewData.add(new FiveWeatherForecast.HourlyWeatherForecast(
                            precipitation_probability.get(i),
                            humidity.get(i),
                            temperatures.get(i),
                            wind_speed.get(i),
                            precipitation.get(i),
                            hourly_time.get(i)));
                }
                hourlyData = hourlyNewData;
                //setup recycle view
                setupRecyclerView();
                updateUI();
            }

            @Override
            public void onError(Throwable t) {
                Log.e("ForecastDataError", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

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
            int temperature = weather.getTemperature();
            double precipitation = weather.getPrecipitation();
            int humidity = weather.getHumidity();
            double windSpeed = weather.getWind_speed();

            // Update temperature
            String condition = getRainStatus(precipitation);
            tvTemperature.setText(String.valueOf(temperature +"°"));
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