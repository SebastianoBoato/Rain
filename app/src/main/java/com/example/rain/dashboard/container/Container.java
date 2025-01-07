package com.example.rain.dashboard.container;

import java.io.Serializable;

public class Container implements Serializable {
    private String id;
    private String name;
    private String shape;
    private Double param1;
    private Double param2;
    private Double height;
    private Double roofArea;
    private Double area;
    private Double totalVolume;
    private Double currentVolume;

    // Costruttore vuoto richiesto da Firebase Firestore
    public Container() {
    }

    public Container(String id, String name, String shape, Double param1, Double height, Double roofArea, Double area, Double totalVolume, Double currentVolume) {
        this.id = id;
        this.name = name;
        this.shape = shape;
        this.param1 = param1;
        this.height = height;
        this.roofArea = roofArea;
        this.area = area;
        this.totalVolume = totalVolume;
        this.currentVolume = currentVolume;
    }

    public Container(String id, String name, String shape, Double param1, Double param2, Double height, Double roofArea, Double area, Double totalVolume, Double currentVolume) {
        this.id = id;
        this.name = name;
        this.shape = shape;
        this.param1 = param1;
        this.param2 = param2;
        this.height = height;
        this.roofArea = roofArea;
        this.area = area;
        this.totalVolume = totalVolume;
        this.currentVolume = currentVolume;
    }

    // Getter e setter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getShape() {
        return shape;
    }

    public Double getParam1() {
        return param1;
    }

    public Double getParam2() {
        return param2;
    }

    public Double getHeight() {
        return height;
    }

    public Double getRoofArea() {
        return roofArea;
    }

    public Double getBaseArea() {
        return area;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public Double getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(Double currentVolume) {
        this.currentVolume = currentVolume;
    }
}
