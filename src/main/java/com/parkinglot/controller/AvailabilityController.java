package com.parkinglot.controller;

import com.parkinglot.dto.AvailabilityRequest;
import com.parkinglot.dto.ParkingSlotResponse;
import com.parkinglot.service.ParkingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@Tag(name = "Availability Check", description = "APIs for checking parking slot availability")
public class AvailabilityController {
    
    private final ParkingSlotService parkingSlotService;
    
    @Operation(summary = "List available slots for a given time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available slots retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid time range")
    })
    @PostMapping
    public ResponseEntity<Page<ParkingSlotResponse>> getAvailableSlots(
            @Valid @RequestBody AvailabilityRequest request,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy) {
        
        Page<ParkingSlotResponse> availableSlots = parkingSlotService.getAvailableSlots(
            request.getStartTime(), 
            request.getEndTime(), 
            request.getVehicleType(),
            page, 
            size, 
            sortBy
        );
        
        return ResponseEntity.ok(availableSlots);
    }
}