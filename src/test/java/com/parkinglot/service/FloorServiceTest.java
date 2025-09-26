package com.parkinglot.service;

import com.parkinglot.dto.FloorCreateRequest;
import com.parkinglot.dto.FloorResponse;
import com.parkinglot.entity.Floor;
import com.parkinglot.exception.DuplicateResourceException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.repository.FloorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FloorServiceTest {
    
    @Mock
    private FloorRepository floorRepository;
    
    @InjectMocks
    private FloorService floorService;
    
    private Floor testFloor;
    private FloorCreateRequest createRequest;
    
    @BeforeEach
    void setUp() {
        testFloor = new Floor(1, "Ground Floor");
        testFloor.setId(1L);
        testFloor.setCreatedAt(LocalDateTime.now());
        testFloor.setUpdatedAt(LocalDateTime.now());
        
        createRequest = new FloorCreateRequest();
        createRequest.setFloorNumber(1);
        createRequest.setFloorName("Ground Floor");
    }
    
    @Test
    void createFloor_ShouldCreateFloor_WhenValidRequest() {
        // Given
        when(floorRepository.existsByFloorNumber(1)).thenReturn(false);
        when(floorRepository.save(any(Floor.class))).thenReturn(testFloor);
        
        // When
        FloorResponse response = floorService.createFloor(createRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1, response.getFloorNumber());
        assertEquals("Ground Floor", response.getFloorName());
        verify(floorRepository).existsByFloorNumber(1);
        verify(floorRepository).save(any(Floor.class));
    }
    
    @Test
    void createFloor_ShouldThrowException_WhenFloorNumberExists() {
        // Given
        when(floorRepository.existsByFloorNumber(1)).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateResourceException.class, 
                    () -> floorService.createFloor(createRequest));
        verify(floorRepository).existsByFloorNumber(1);
        verify(floorRepository, never()).save(any(Floor.class));
    }
    
    @Test
    void getAllFloors_ShouldReturnAllFloors() {
        // Given
        Floor floor2 = new Floor(2, "First Floor");
        floor2.setId(2L);
        when(floorRepository.findAll()).thenReturn(Arrays.asList(testFloor, floor2));
        
        // When
        List<FloorResponse> responses = floorService.getAllFloors();
        
        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        verify(floorRepository).findAll();
    }
    
    @Test
    void getFloorById_ShouldReturnFloor_WhenExists() {
        // Given
        when(floorRepository.findById(1L)).thenReturn(Optional.of(testFloor));
        
        // When
        FloorResponse response = floorService.getFloorById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1, response.getFloorNumber());
        assertEquals("Ground Floor", response.getFloorName());
        verify(floorRepository).findById(1L);
    }
    
    @Test
    void getFloorById_ShouldThrowException_WhenNotExists() {
        // Given
        when(floorRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class,
                    () -> floorService.getFloorById(1L));
        verify(floorRepository).findById(1L);
    }
    
    @Test
    void getFloorEntity_ShouldReturnEntity_WhenExists() {
        // Given
        when(floorRepository.findById(1L)).thenReturn(Optional.of(testFloor));
        
        // When
        Floor result = floorService.getFloorEntity(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testFloor, result);
        verify(floorRepository).findById(1L);
    }
    
    @Test
    void getFloorEntity_ShouldThrowException_WhenNotExists() {
        // Given
        when(floorRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class,
                    () -> floorService.getFloorEntity(1L));
        verify(floorRepository).findById(1L);
    }
    
    @Test
    void getFloorWithSlots_ShouldReturnFloorWithSlots() {
        // Given
        when(floorRepository.findByIdWithSlots(1L)).thenReturn(Optional.of(testFloor));
        
        // When
        FloorResponse response = floorService.getFloorWithSlots(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(floorRepository).findByIdWithSlots(1L);
    }
}