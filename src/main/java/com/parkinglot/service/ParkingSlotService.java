package com.parkinglot.service;

import com.parkinglot.dto.ParkingSlotCreateRequest;
import com.parkinglot.dto.ParkingSlotResponse;
import com.parkinglot.entity.Floor;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.exception.DuplicateResourceException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.model.VehicleType;
import com.parkinglot.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParkingSlotService {
    
    private final ParkingSlotRepository parkingSlotRepository;
    private final FloorService floorService;
    
    public ParkingSlotResponse createParkingSlot(ParkingSlotCreateRequest request) {
        log.info("Creating parking slot {} on floor {}", request.getSlotNumber(), request.getFloorId());
        
        Floor floor = floorService.getFloorEntity(request.getFloorId());
        
        if (parkingSlotRepository.existsByFloorIdAndSlotNumber(request.getFloorId(), request.getSlotNumber())) {
            throw new DuplicateResourceException(
                "Parking slot " + request.getSlotNumber() + " already exists on floor " + request.getFloorId());
        }
        
        ParkingSlot slot = new ParkingSlot(request.getSlotNumber(), floor, request.getVehicleType());
        ParkingSlot savedSlot = parkingSlotRepository.save(slot);
        
        // Update floor total slots count
        floor.setTotalSlots(floor.getTotalSlots() + 1);
        
        log.info("Parking slot created successfully with ID: {}", savedSlot.getId());
        return convertToResponse(savedSlot);
    }
    
    @Transactional(readOnly = true)
    public List<ParkingSlotResponse> getAllParkingSlots() {
        log.info("Fetching all parking slots");
        return parkingSlotRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ParkingSlotResponse getParkingSlotById(Long id) {
        log.info("Fetching parking slot with ID: {}", id);
        ParkingSlot slot = parkingSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found with ID: " + id));
        return convertToResponse(slot);
    }
    
    @Transactional(readOnly = true)
    public List<ParkingSlotResponse> getParkingSlotsByFloor(Long floorId) {
        log.info("Fetching parking slots for floor ID: {}", floorId);
        return parkingSlotRepository.findByFloorId(floorId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<ParkingSlotResponse> getAvailableSlots(LocalDateTime startTime, LocalDateTime endTime, 
                                                       VehicleType vehicleType, int page, int size, String sortBy) {
        log.info("Fetching available slots from {} to {} for vehicle type: {}", startTime, endTime, vehicleType);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        
        if (vehicleType != null) {
            return parkingSlotRepository.findAvailableSlotsByVehicleType(vehicleType, startTime, endTime, pageable)
                    .map(this::convertToResponse);
        } else {
            List<ParkingSlot> availableSlots = parkingSlotRepository.findAvailableSlots(startTime, endTime);
            // Manual pagination for the simple case
            int start = page * size;
            int end = Math.min(start + size, availableSlots.size());
            List<ParkingSlotResponse> pagedSlots = availableSlots.subList(start, end)
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return new org.springframework.data.domain.PageImpl<>(pagedSlots, pageable, availableSlots.size());
        }
    }
    
    public ParkingSlot getParkingSlotEntity(Long id) {
        return parkingSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found with ID: " + id));
    }
    
    private ParkingSlotResponse convertToResponse(ParkingSlot slot) {
        ParkingSlotResponse response = new ParkingSlotResponse();
        response.setId(slot.getId());
        response.setSlotNumber(slot.getSlotNumber());
        response.setFloorId(slot.getFloor().getId());
        response.setFloorName(slot.getFloor().getFloorName());
        response.setVehicleType(slot.getVehicleType());
        response.setStatus(slot.getStatus());
        response.setCreatedAt(slot.getCreatedAt());
        response.setUpdatedAt(slot.getUpdatedAt());
        return response;
    }
}