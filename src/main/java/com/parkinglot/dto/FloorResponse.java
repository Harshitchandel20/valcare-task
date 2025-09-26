package com.parkinglot.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FloorResponse {
    private Long id;
    private Integer floorNumber;
    private String floorName;
    private Integer totalSlots;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ParkingSlotResponse> parkingSlots;
}