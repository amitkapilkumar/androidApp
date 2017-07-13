package com.CdCdoranCoUk.CdCdoranCoUkSProximit4O1.model;

import com.estimote.sdk.cloud.model.Color;

/**
 * Created by cognizant on 28/04/2017.
 */

public class BeaconDescription {
    private Double temperature;
    private String name;
    private Color color;

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}