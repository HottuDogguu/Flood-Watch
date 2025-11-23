package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.FiveWeatherForecast;
import com.example.myapplication.utils.GlobalUtility;

import java.util.List;
import java.util.Locale;

public class WeatherHourTimelineAdapter extends RecyclerView.Adapter<WeatherHourTimelineAdapter.VH> {

    private List<FiveWeatherForecast.HourlyWeatherForecast> hourly;
    private GlobalUtility globalUtility;

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTime, tvTemp, tvRain, tvHumidity, tvCondition;
        ImageView ivIcon;

        VH(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTemp = itemView.findViewById(R.id.tv_temp);
            tvRain = itemView.findViewById(R.id.tv_rain);
            tvHumidity = itemView.findViewById(R.id.tv_humidity);
            tvCondition = itemView.findViewById(R.id.tv_condition);
            ivIcon = itemView.findViewById(R.id.iv_weather_icon);
        }
    }

    public WeatherHourTimelineAdapter(List<FiveWeatherForecast.HourlyWeatherForecast> hours) {
        this.hourly = hours;
        this.globalUtility = new GlobalUtility();

    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather_hour_dashboard, parent, false);
        return new VH(v);
    }


    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        FiveWeatherForecast.HourlyWeatherForecast weather = hourly.get(pos);


        String forecastHour = globalUtility.formatDateIntoHourOnly(weather.getForecast_time());
        double temperature = weather.getTemperature();
        double precipitation = weather.getPrecipitation();
        int humidity = weather.getHumidity();

        String condition = handleWeatherCondition(precipitation);


        holder.tvTime.setText(forecastHour);
        holder.tvTemp.setText(String.format(Locale.getDefault(),
                "%.2f°", temperature));
        holder.tvRain.setText(String.format(Locale.getDefault(),
                "%.1fmm", precipitation));
        holder.tvHumidity.setText(String.format(Locale.getDefault(),
                "%d%%", humidity));
        holder.tvCondition.setText(condition);

        // Set weather icon based on rainfall
        if (precipitation > 1.5) {
            holder.ivIcon.setImageResource(R.drawable.ic_rain);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_cloud);
        }
    }

    private String handleWeatherCondition(double precipitation) {
        if (precipitation >= 10) {
            return "Heavy Rain";
        } else if (precipitation >= 2.5) {
            return "Rainy";

        } else if (precipitation > 0) {
            return "Light Rain";
        } else {

            return "Clear";
        }
    }

    @Override
    public int getItemCount() {
        return hourly.size();
    }
}
