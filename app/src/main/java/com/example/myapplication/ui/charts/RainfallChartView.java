package com.example.myapplication.ui.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RainfallChartView extends View {

    private float[] data;
    private Paint barPaint;
    private Paint textPaint;
    private Paint labelPaint;
    private float maxValue = 3.0f;

    public RainfallChartView(Context context) {
        super(context);
        init();
    }

    public RainfallChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#2563EB"));
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#1F2937"));
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        labelPaint = new Paint();
        labelPaint.setColor(Color.parseColor("#9CA3AF"));
        labelPaint.setTextSize(24f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setAntiAlias(true);
    }

    public void setData(float[] data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.length == 0) return;

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int barSpacing = 16;
        int availableWidth = width - (2 * padding);
        int barWidth = (availableWidth - (barSpacing * (data.length - 1))) / data.length;
        int chartHeight = height - (2 * padding);

        for (int i = 0; i < data.length; i++) {
            float barHeight = (data[i] / maxValue) * chartHeight;
            if (barHeight < 30) barHeight = 30; // Minimum height

            float left = padding + (i * (barWidth + barSpacing));
            float top = height - padding - barHeight;
            float right = left + barWidth;
            float bottom = height - padding;

            // Draw bar with rounded top
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, 12, 12, barPaint);

            // Draw value on top of bar
            canvas.drawText(String.format("%.1f", data[i]),
                    left + (barWidth / 2f),
                    top - 10,
                    textPaint);

            // Draw hour label below bar
            String label = (12 + i) + "h";
            canvas.drawText(label,
                    left + (barWidth / 2f),
                    height - 5,
                    labelPaint);
        }
    }
}