package com.example.rain.items;

public class WeatherItem {

    private final String time;

    private final String condition;
    // TODO: assolutamente l'icona da aggiungere

    private final double temp;
    private final int chanceOfRain;
    private final double precip;

    public WeatherItem(String time, String condition, double temp, int chanceOfRain, double precip) {
        this.time = time;
        this.condition = condition;
        this.temp = temp;
        this.chanceOfRain = chanceOfRain;
        this.precip = precip;
    }

    public String getTime() {
        return time;
    }

    public String getCondition() {
        return condition;
    }

    public double getTemp() {
        return temp;
    }

    public int getChanceOfRain() {
        return chanceOfRain;
    }

    public double getPrecip() {
        return precip;
    }
}
