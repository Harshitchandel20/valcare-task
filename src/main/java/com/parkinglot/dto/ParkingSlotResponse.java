package com.parkinglot.dto;

import com.parkinglot.model.SlotStatus;
import com.parkinglot.model.VehicleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingSlotResponse {
    private Long id;
    private String slotNumber;
    private Long floorId;
    private String floorName;
    private VehicleType vehicleType;
    private SlotStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}