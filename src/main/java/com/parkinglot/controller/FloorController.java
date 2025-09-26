package com.parkinglot.controller;

import com.parkinglot.dto.FloorCreateRequest;
import com.parkinglot.dto.FloorResponse;
import com.parkinglot.service.FloorService;
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
@RequestMapping("/api/floors")
@RequiredArgsConstructor
@Tag(name = "Floor Management", description = "APIs for managing parking floors")
public class FloorController {
    
    private final FloorService floorService;
    
    @Operation(summary = "Create a new parking floor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Floor created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Floor number already exists")
    })
    @PostMapping
    public ResponseEntity<FloorResponse> createFloor(@Valid @RequestBody FloorCreateRequest request) {
        FloorResponse response = floorService.createFloor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get all parking floors")
    @ApiResponse(responseCode = "200", description = "Floors retrieved successfully")
    @GetMapping
    public ResponseEntity<List<FloorResponse>> getAllFloors() {
        List<FloorResponse> floors = floorService.getAllFloors();
        return ResponseEntity.ok(floors);
    }
    
    @Operation(summary = "Get floor by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Floor found"),
        @ApiResponse(responseCode = "404", description = "Floor not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FloorResponse> getFloorById(@PathVariable Long id) {
        FloorResponse floor = floorService.getFloorById(id);
        return ResponseEntity.ok(floor);
    }
    
    @Operation(summary = "Get floor with all its parking slots")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Floor with slots retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Floor not found")
    })
    @GetMapping("/{id}/slots")
    public ResponseEntity<FloorResponse> getFloorWithSlots(@PathVariable Long id) {
        FloorResponse floor = floorService.getFloorWithSlots(id);
        return ResponseEntity.ok(floor);
    }
}