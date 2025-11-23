package com.example.myapplication.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.FiveWeatherForecast;
import com.example.myapplication.utils.GlobalUtility;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    private List<FiveWeatherForecast.HourlyWeatherForecast> hourlyData;
    private GlobalUtility globalUtility;

    public HourlyForecastAdapter(List<FiveWeatherForecast.HourlyWeatherForecast> hourlyData) {
        this.hourlyData = hourlyData;
        globalUtility = new GlobalUtility();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FiveWeatherForecast.HourlyWeatherForecast weather = hourlyData.get(position);


        String forecastHour = globalUtility.formatDateIntoHourOnly(weather.getForecast_time());
        double temperature = weather.getTemperature();
        double precipitation = weather.getPrecipitation();
        int humidity = weather.getHumidity();

        String condition = handleWeatherCondition(precipitation);


        holder.tvHour.setText(forecastHour);
        holder.tvTemperature.setText(String.format(Locale.getDefault(),
                "%.2f°", temperature));
        holder.tvRainfall.setText(String.format(Locale.getDefault(),
                "%.1fmm", precipitation));
        holder.tvHumidity.setText(String.format(Locale.getDefault(),
                "%d%%",humidity ));
        holder.tvCondition.setText(condition);

        // Set weather icon based on rainfall
        if (precipitation > 1.5) {
            holder.ivWeatherIcon.setImageResource(R.drawable.ic_rain);
        } else {
            holder.ivWeatherIcon.setImageResource(R.drawable.ic_cloud);
        }
    }

    private String handleWeatherCondition(double precipitation){
        if(precipitation >= 10){
            return "Heavy Rain";
        }else if(precipitation >= 2.5){
            return "Rainy";

        }else if (precipitation >0){
            return "Light Rain";
        }else{

            return "Clear";
        }

    }

    @Override
    public int getItemCount() {
        return hourlyData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHour, tvTemperature, tvRainfall, tvHumidity, tvCondition;
        ImageView ivWeatherIcon;

        ViewHolder(View itemView) {
            super(itemView);
            tvHour = itemView.findViewById(R.id.tv_hour);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvRainfall = itemView.findViewById(R.id.tv_rainfall);
            tvHumidity = itemView.findViewById(R.id.tv_humidity);
            tvCondition = itemView.findViewById(R.id.tv_condition);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);
        }
    }
}