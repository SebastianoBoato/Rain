package com.example.rain.items;

import android.os.Parcel;
import android.os.Parcelable;

public class DailyWeatherItem implements Parcelable {

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


    // tutta sta cosa sotto serve per renderla passabile tra attività diverse

    protected DailyWeatherItem(Parcel in) {
        condition = in.readString();
        iconUrl = in.readString();
        maxTemp = in.readDouble();
        minTemp = in.readDouble();
        avgTemp = in.readDouble();
        chanceOfRain = in.readInt();
        precip = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(condition);
        dest.writeString(iconUrl);
        dest.writeDouble(maxTemp);
        dest.writeDouble(minTemp);
        dest.writeDouble(avgTemp);
        dest.writeInt(chanceOfRain);
        dest.writeDouble(precip);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DailyWeatherItem> CREATOR = new Creator<DailyWeatherItem>() {
        @Override
        public DailyWeatherItem createFromParcel(Parcel in) {
            return new DailyWeatherItem(in);
        }

        @Override
        public DailyWeatherItem[] newArray(int size) {
            return new DailyWeatherItem[size];
        }
    };
}
