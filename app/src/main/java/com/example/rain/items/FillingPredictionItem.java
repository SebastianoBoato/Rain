package com.example.rain.items;

public class FillingPredictionItem {

    private final String containerName;
    private final double containerTotalVolume;
    private final double containerCurrentVolume;
    private final double containerVolumeIncrease;
    private final double containerPredictionVolume;
    private final String containerShape;

    public FillingPredictionItem(String containerName, double containerTotalVolume, double containerCurrentVolume, double containerVolumeIncrease, double containerPredictionVolume, String containerShape) {
        this.containerName = containerName;
        this.containerTotalVolume = containerTotalVolume;
        this.containerCurrentVolume = containerCurrentVolume;
        this.containerVolumeIncrease = containerVolumeIncrease;
        this.containerPredictionVolume = containerPredictionVolume;
        this.containerShape = containerShape;
    }

    public String getContainerName() {
        return containerName;
    }

    public double getContainerTotalVolume() {
        return containerTotalVolume;
    }

    public double getContainerCurrentVolume() {
        return containerCurrentVolume;
    }

    public double getContainerVolumeIncrease() {
        return containerVolumeIncrease;
    }

    public double getContainerPredictionVolume() {
        return containerPredictionVolume;
    }

    public String getContainerShape() {
        return containerShape;
    }
}
