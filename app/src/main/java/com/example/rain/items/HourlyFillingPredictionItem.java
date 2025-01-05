package com.example.rain.items;

public class HourlyFillingPredictionItem {
    private final String hour;
    private final double predictionVolume;

    public HourlyFillingPredictionItem(String hour, double predictionVolume) {
        this.hour = hour;
        this.predictionVolume = predictionVolume;
    }

    public String getHour() {
        return hour;
    }

    public double getPredictionVolume() {
        return predictionVolume;
    }
}
