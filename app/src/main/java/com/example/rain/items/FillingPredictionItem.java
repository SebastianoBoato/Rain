package com.example.rain.items;

public class FillingPredictionItem {

    private final String containerName;
    private final double containerTotalVolume;
    private final double containerCurrentVolume;
    private final double containerVolumeIncrease;
    private final double containerPredictionVolume;
    //TODO: container shape

    public FillingPredictionItem(String containerName, double containerTotalVolume, double containerCurrentVolume, double containerVolumeIncrease, double containerPredictionVolume) {
        this.containerName = containerName;
        this.containerTotalVolume = containerTotalVolume;
        this.containerCurrentVolume = containerCurrentVolume;
        this.containerVolumeIncrease = containerVolumeIncrease;
        this.containerPredictionVolume = containerPredictionVolume;
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
}
