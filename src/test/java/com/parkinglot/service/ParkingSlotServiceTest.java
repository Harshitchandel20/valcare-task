package com.parkinglot.service;

import com.parkinglot.dto.ParkingSlotCreateRequest;
import com.parkinglot.dto.ParkingSlotResponse;
import com.parkinglot.entity.Floor;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.exception.DuplicateResourceException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.model.SlotStatus;
import com.parkinglot.model.VehicleType;
import com.parkinglot.repository.ParkingSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSlotServiceTest {
    
    @Mock
    private ParkingSlotRepository parkingSlotRepository;
    
    @Mock
    private FloorService floorService;
    
    @InjectMocks
    private ParkingSlotService parkingSlotService;
    
    private Floor testFloor;
    private ParkingSlot testSlot;
    private ParkingSlotCreateRequest createRequest;
    
    @BeforeEach
    void setUp() {
        testFloor = new Floor(1, "Ground Floor");
        testFloor.setId(1L);
        testFloor.setTotalSlots(0);
        
        testSlot = new ParkingSlot("A1", testFloor, VehicleType.FOUR_WHEELER);
        testSlot.setId(1L);
        testSlot.setStatus(SlotStatus.AVAILABLE);
        testSlot.setCreatedAt(LocalDateTime.now());
        testSlot.setUpdatedAt(LocalDateTime.now());
        
        createRequest = new ParkingSlotCreateRequest();
        createRequest.setFloorId(1L);
        createRequest.setSlotNumber("A1");
        createRequest.setVehicleType(VehicleType.FOUR_WHEELER);
    }
    
    @Test
    void createParkingSlot_ShouldCreateSlot_WhenValidRequest() {
        // Given
        when(floorService.getFloorEntity(1L)).thenReturn(testFloor);
        when(parkingSlotRepository.existsByFloorIdAndSlotNumber(1L, "A1")).thenReturn(false);
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenReturn(testSlot);
        
        // When
        ParkingSlotResponse response = parkingSlotService.createParkingSlot(createRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("A1", response.getSlotNumber());
        assertEquals(VehicleType.FOUR_WHEELER, response.getVehicleType());
        assertEquals(SlotStatus.AVAILABLE, response.getStatus());
        verify(floorService).getFloorEntity(1L);
        verify(parkingSlotRepository).existsByFloorIdAndSlotNumber(1L, "A1");
        verify(parkingSlotRepository).save(any(ParkingSlot.class));
    }
    
    @Test
    void createParkingSlot_ShouldThrowException_WhenSlotExists() {
        // Given
        when(floorService.getFloorEntity(1L)).thenReturn(testFloor);
        when(parkingSlotRepository.existsByFloorIdAndSlotNumber(1L, "A1")).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateResourceException.class,
                    () -> parkingSlotService.createParkingSlot(createRequest));
        verify(parkingSlotRepository, never()).save(any(ParkingSlot.class));
    }
    
    @Test
    void getAllParkingSlots_ShouldReturnAllSlots() {
        // Given
        when(parkingSlotRepository.findAll()).thenReturn(Arrays.asList(testSlot));
        
        // When
        List<ParkingSlotResponse> responses = parkingSlotService.getAllParkingSlots();
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        verify(parkingSlotRepository).findAll();
    }
    
    @Test
    void getParkingSlotById_ShouldReturnSlot_WhenExists() {
        // Given
        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(testSlot));
        
        // When
        ParkingSlotResponse response = parkingSlotService.getParkingSlotById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("A1", response.getSlotNumber());
        verify(parkingSlotRepository).findById(1L);
    }
    
    @Test
    void getParkingSlotById_ShouldThrowException_WhenNotExists() {
        // Given
        when(parkingSlotRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class,
                    () -> parkingSlotService.getParkingSlotById(1L));
        verify(parkingSlotRepository).findById(1L);
    }
    
    @Test
    void getAvailableSlots_ShouldReturnAvailableSlots_WithVehicleTypeFilter() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        Page<ParkingSlot> slotsPage = new PageImpl<>(Arrays.asList(testSlot));
        when(parkingSlotRepository.findAvailableSlotsByVehicleType(
                eq(VehicleType.FOUR_WHEELER), eq(startTime), eq(endTime), any(PageRequest.class)))
                .thenReturn(slotsPage);
        
        // When
        Page<ParkingSlotResponse> responses = parkingSlotService.getAvailableSlots(
                startTime, endTime, VehicleType.FOUR_WHEELER, 0, 10, "id");
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.getContent().size());
        assertEquals(1L, responses.getContent().get(0).getId());
        verify(parkingSlotRepository).findAvailableSlotsByVehicleType(
                eq(VehicleType.FOUR_WHEELER), eq(startTime), eq(endTime), any(PageRequest.class));
    }
    
    @Test
    void getAvailableSlots_ShouldReturnAvailableSlots_WithoutVehicleTypeFilter() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        when(parkingSlotRepository.findAvailableSlots(startTime, endTime))
                .thenReturn(Arrays.asList(testSlot));
        
        // When
        Page<ParkingSlotResponse> responses = parkingSlotService.getAvailableSlots(
                startTime, endTime, null, 0, 10, "id");
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.getContent().size());
        assertEquals(1L, responses.getContent().get(0).getId());
        verify(parkingSlotRepository).findAvailableSlots(startTime, endTime);
    }
}