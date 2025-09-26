package com.parkinglot.service;

import com.parkinglot.dto.ReservationCreateRequest;
import com.parkinglot.dto.ReservationResponse;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.Reservation;
import com.parkinglot.exception.BusinessRuleViolationException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.model.ReservationStatus;
import com.parkinglot.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final ParkingSlotService parkingSlotService;
    
    public ReservationResponse createReservation(ReservationCreateRequest request) {
        log.info("Creating reservation for slot {} from {} to {}", 
                request.getParkingSlotId(), request.getStartTime(), request.getEndTime());
        
        // Validate business rules
        validateReservationRequest(request);
        
        ParkingSlot slot = parkingSlotService.getParkingSlotEntity(request.getParkingSlotId());
        
        // Check for conflicts
        if (reservationRepository.hasConflictingReservation(
                request.getParkingSlotId(), request.getStartTime(), request.getEndTime())) {
            throw new BusinessRuleViolationException(
                "Slot is already reserved for the specified time range");
        }
        
        // Verify vehicle type matches slot
        if (!slot.getVehicleType().equals(request.getVehicleType())) {
            throw new BusinessRuleViolationException(
                "Vehicle type " + request.getVehicleType() + " does not match slot type " + slot.getVehicleType());
        }
        
        Reservation reservation = new Reservation(
            slot, request.getVehicleNumber(), request.getVehicleType(), 
            request.getStartTime(), request.getEndTime());
        
        // Calculate cost and duration
        calculateCostAndDuration(reservation);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        log.info("Reservation created successfully with ID: {}", savedReservation.getId());
        return convertToResponse(savedReservation);
    }
    
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        log.info("Fetching reservation with ID: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));
        return convertToResponse(reservation);
    }
    
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        log.info("Fetching all reservations");
        return reservationRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservationResponse> getActiveReservations() {
        log.info("Fetching active reservations");
        return reservationRepository.findByStatus(ReservationStatus.ACTIVE)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ReservationResponse cancelReservation(Long id) {
        log.info("Cancelling reservation with ID: {}", id);
        
        Reservation reservation = reservationRepository.findByIdAndStatus(id, ReservationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active reservation not found with ID: " + id));
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationRepository.save(reservation);
        
        log.info("Reservation cancelled successfully with ID: {}", id);
        return convertToResponse(savedReservation);
    }
    
    private void validateReservationRequest(ReservationCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check if start time is before end time
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BusinessRuleViolationException("Start time must be before end time");
        }
        
        // Check if times are in the future
        if (request.getStartTime().isBefore(now) || request.getEndTime().isBefore(now)) {
            throw new BusinessRuleViolationException("Reservation times must be in the future");
        }
        
        // Check duration doesn't exceed 24 hours
        Duration duration = Duration.between(request.getStartTime(), request.getEndTime());
        if (duration.toHours() > 24) {
            throw new BusinessRuleViolationException("Reservation duration cannot exceed 24 hours");
        }
    }
    
    private void calculateCostAndDuration(Reservation reservation) {
        Duration duration = Duration.between(reservation.getStartTime(), reservation.getEndTime());
        
        // Round up partial hours
        long hours = duration.toHours();
        if (duration.toMinutes() % 60 > 0) {
            hours++;
        }
        
        reservation.setDurationHours((int) hours);
        
        // Calculate total cost
        double hourlyRate = reservation.getVehicleType().getHourlyRate();
        BigDecimal totalCost = BigDecimal.valueOf(hourlyRate * hours);
        reservation.setTotalCost(totalCost);
        
        log.info("Calculated duration: {} hours, cost: {}", hours, totalCost);
    }
    
    private ReservationResponse convertToResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setParkingSlotId(reservation.getParkingSlot().getId());
        response.setSlotNumber(reservation.getParkingSlot().getSlotNumber());
        response.setFloorName(reservation.getParkingSlot().getFloor().getFloorName());
        response.setVehicleNumber(reservation.getVehicleNumber());
        response.setVehicleType(reservation.getVehicleType());
        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getEndTime());
        response.setTotalCost(reservation.getTotalCost());
        response.setDurationHours(reservation.getDurationHours());
        response.setStatus(reservation.getStatus());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());
        return response;
    }
}