package com.example.rain.items;

public class DailyWeatherItem {

    private final String condition;
    private final String iconUrl;

    private final double maxTemp;
    private final double minTemp;
    private final double avgTemp;

    private final int chanceOfRain;
    private final double precip;

    public DailyWeatherItem(String condition, String iconUrl, double maxTemp, double minTemp, double avgTemp, int chanceOfRain, double precip) {
        this.condition = condition;
        this.iconUrl = iconUrl;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.avgTemp = avgTemp;
        this.chanceOfRain = chanceOfRain;
        this.precip = precip;
    }

    public String getCondition() {
        return condition;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getAvgTemp() {
        return avgTemp;
    }

    public int getChanceOfRain() {
        return chanceOfRain;
    }

    public double getPrecip() {
        return precip;
    }
}
