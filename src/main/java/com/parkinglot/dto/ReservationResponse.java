package com.parkinglot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.parkinglot.model.ReservationStatus;
import com.parkinglot.model.VehicleType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private Long id;
    private Long parkingSlotId;
    private String slotNumber;
    private String floorName;
    private String vehicleNumber;
    private VehicleType vehicleType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    private BigDecimal totalCost;
    private Integer durationHours;
    private ReservationStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}