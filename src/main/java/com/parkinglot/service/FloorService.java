package com.parkinglot.service;

import com.parkinglot.dto.FloorCreateRequest;
import com.parkinglot.dto.FloorResponse;
import com.parkinglot.entity.Floor;
import com.parkinglot.exception.DuplicateResourceException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.repository.FloorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FloorService {
    
    private final FloorRepository floorRepository;
    
    public FloorResponse createFloor(FloorCreateRequest request) {
        log.info("Creating floor with number: {}", request.getFloorNumber());
        
        if (floorRepository.existsByFloorNumber(request.getFloorNumber())) {
            throw new DuplicateResourceException("Floor with number " + request.getFloorNumber() + " already exists");
        }
        
        Floor floor = new Floor(request.getFloorNumber(), request.getFloorName());
        Floor savedFloor = floorRepository.save(floor);
        
        log.info("Floor created successfully with ID: {}", savedFloor.getId());
        return convertToResponse(savedFloor);
    }
    
    @Transactional(readOnly = true)
    public List<FloorResponse> getAllFloors() {
        log.info("Fetching all floors");
        return floorRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public FloorResponse getFloorById(Long id) {
        log.info("Fetching floor with ID: {}", id);
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found with ID: " + id));
        return convertToResponse(floor);
    }
    
    @Transactional(readOnly = true)
    public FloorResponse getFloorWithSlots(Long id) {
        log.info("Fetching floor with slots for ID: {}", id);
        Floor floor = floorRepository.findByIdWithSlots(id)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found with ID: " + id));
        return convertToResponseWithSlots(floor);
    }
    
    public Floor getFloorEntity(Long id) {
        return floorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found with ID: " + id));
    }
    
    private FloorResponse convertToResponse(Floor floor) {
        FloorResponse response = new FloorResponse();
        response.setId(floor.getId());
        response.setFloorNumber(floor.getFloorNumber());
        response.setFloorName(floor.getFloorName());
        response.setTotalSlots(floor.getTotalSlots());
        response.setCreatedAt(floor.getCreatedAt());
        response.setUpdatedAt(floor.getUpdatedAt());
        return response;
    }
    
    private FloorResponse convertToResponseWithSlots(Floor floor) {
        FloorResponse response = convertToResponse(floor);
        if (floor.getParkingSlots() != null) {
            response.setParkingSlots(floor.getParkingSlots().stream()
                    .map(slot -> {
                        com.parkinglot.dto.ParkingSlotResponse slotResponse = new com.parkinglot.dto.ParkingSlotResponse();
                        slotResponse.setId(slot.getId());
                        slotResponse.setSlotNumber(slot.getSlotNumber());
                        slotResponse.setFloorId(slot.getFloor().getId());
                        slotResponse.setFloorName(slot.getFloor().getFloorName());
                        slotResponse.setVehicleType(slot.getVehicleType());
                        slotResponse.setStatus(slot.getStatus());
                        slotResponse.setCreatedAt(slot.getCreatedAt());
                        slotResponse.setUpdatedAt(slot.getUpdatedAt());
                        return slotResponse;
                    })
                    .collect(Collectors.toList()));
        }
        return response;
    }
}