package com.parkinglot.controller;

import com.parkinglot.dto.ParkingSlotCreateRequest;
import com.parkinglot.dto.ParkingSlotResponse;
import com.parkinglot.service.ParkingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@Tag(name = "Parking Slot Management", description = "APIs for managing parking slots")
public class ParkingSlotController {
    
    private final ParkingSlotService parkingSlotService;
    
    @Operation(summary = "Create parking slots for a floor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Slot created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Floor not found"),
        @ApiResponse(responseCode = "409", description = "Slot number already exists on floor")
    })
    @PostMapping
    public ResponseEntity<ParkingSlotResponse> createParkingSlot(@Valid @RequestBody ParkingSlotCreateRequest request) {
        ParkingSlotResponse response = parkingSlotService.createParkingSlot(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get all parking slots")
    @ApiResponse(responseCode = "200", description = "Slots retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ParkingSlotResponse>> getAllParkingSlots() {
        List<ParkingSlotResponse> slots = parkingSlotService.getAllParkingSlots();
        return ResponseEntity.ok(slots);
    }
    
    @Operation(summary = "Get parking slot by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Slot found"),
        @ApiResponse(responseCode = "404", description = "Slot not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlotResponse> getParkingSlotById(@PathVariable Long id) {
        ParkingSlotResponse slot = parkingSlotService.getParkingSlotById(id);
        return ResponseEntity.ok(slot);
    }
    
    @Operation(summary = "Get parking slots by floor")
    @ApiResponse(responseCode = "200", description = "Floor slots retrieved successfully")
    @GetMapping("/floor/{floorId}")
    public ResponseEntity<List<ParkingSlotResponse>> getParkingSlotsByFloor(@PathVariable Long floorId) {
        List<ParkingSlotResponse> slots = parkingSlotService.getParkingSlotsByFloor(floorId);
        return ResponseEntity.ok(slots);
    }
}