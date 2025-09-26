package com.parkinglot.model;

public enum VehicleType {
    TWO_WHEELER("2 wheeler", 20.0),
    FOUR_WHEELER("4 wheeler", 30.0);
    
    private final String displayName;
    private final Double hourlyRate;
    
    VehicleType(String displayName, Double hourlyRate) {
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Double getHourlyRate() {
        return hourlyRate;
    }
}