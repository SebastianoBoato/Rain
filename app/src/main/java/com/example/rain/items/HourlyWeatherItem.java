package com.example.rain.items;

import android.os.Parcel;
import android.os.Parcelable;

public class HourlyWeatherItem implements Parcelable {

    private final String time;
    private final String condition;
    private final String iconUrl;
    private final double temp;
    private final int chanceOfRain;
    private final double precip;

    public HourlyWeatherItem(String time, String condition, String iconUrl, double temp, int chanceOfRain, double precip) {
        this.time = time;
        this.condition = condition;
        this.iconUrl = iconUrl;
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

    public String getIconUrl() {
        return iconUrl;
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


    // tutta sta cosa sotto serve per renderla passabile tra attività diverse

    protected HourlyWeatherItem(Parcel in) {
        time = in.readString();
        condition = in.readString();
        iconUrl = in.readString();
        temp = in.readDouble();
        chanceOfRain = in.readInt();
        precip = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(condition);
        dest.writeString(iconUrl);
        dest.writeDouble(temp);
        dest.writeInt(chanceOfRain);
        dest.writeDouble(precip);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HourlyWeatherItem> CREATOR = new Creator<HourlyWeatherItem>() {
        @Override
        public HourlyWeatherItem createFromParcel(Parcel in) {
            return new HourlyWeatherItem(in);
        }

        @Override
        public HourlyWeatherItem[] newArray(int size) {
            return new HourlyWeatherItem[size];
        }
    };
}
