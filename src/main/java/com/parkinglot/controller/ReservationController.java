package com.parkinglot.controller;

import com.parkinglot.dto.ReservationCreateRequest;
import com.parkinglot.dto.ReservationResponse;
import com.parkinglot.service.ReservationService;
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
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Management", description = "APIs for managing parking reservations")
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @Operation(summary = "Reserve a parking slot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
        @ApiResponse(responseCode = "404", description = "Parking slot not found"),
        @ApiResponse(responseCode = "409", description = "Slot already reserved for specified time")
    })
    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationCreateRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get reservation details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation found"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        ReservationResponse reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }
    
    @Operation(summary = "Get all reservations")
    @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }
    
    @Operation(summary = "Get all active reservations")
    @ApiResponse(responseCode = "200", description = "Active reservations retrieved successfully")
    @GetMapping("/active")
    public ResponseEntity<List<ReservationResponse>> getActiveReservations() {
        List<ReservationResponse> reservations = reservationService.getActiveReservations();
        return ResponseEntity.ok(reservations);
    }
    
    @Operation(summary = "Cancel a reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Active reservation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        ReservationResponse reservation = reservationService.cancelReservation(id);
        return ResponseEntity.ok(reservation);
    }
}