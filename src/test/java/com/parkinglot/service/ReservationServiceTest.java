package com.parkinglot.service;

import com.parkinglot.dto.ReservationCreateRequest;
import com.parkinglot.dto.ReservationResponse;
import com.parkinglot.entity.Floor;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.Reservation;
import com.parkinglot.exception.BusinessRuleViolationException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.model.ReservationStatus;
import com.parkinglot.model.SlotStatus;
import com.parkinglot.model.VehicleType;
import com.parkinglot.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    
    @Mock
    private ReservationRepository reservationRepository;
    
    @Mock
    private ParkingSlotService parkingSlotService;
    
    @InjectMocks
    private ReservationService reservationService;
    
    private Reservation testReservation;
    private ParkingSlot testSlot;
    private Floor testFloor;
    private ReservationCreateRequest createRequest;
    
    @BeforeEach
    void setUp() {
        testFloor = new Floor(1, "Ground Floor");
        testFloor.setId(1L);
        
        testSlot = new ParkingSlot("A1", testFloor, VehicleType.FOUR_WHEELER);
        testSlot.setId(1L);
        testSlot.setStatus(SlotStatus.AVAILABLE);
        
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        
        testReservation = new Reservation(testSlot, "KA05MH1234", VehicleType.FOUR_WHEELER, startTime, endTime);
        testReservation.setId(1L);
        testReservation.setTotalCost(BigDecimal.valueOf(60.0));
        testReservation.setDurationHours(2);
        
        createRequest = new ReservationCreateRequest();
        createRequest.setParkingSlotId(1L);
        createRequest.setVehicleNumber("KA05MH1234");
        createRequest.setVehicleType(VehicleType.FOUR_WHEELER);
        createRequest.setStartTime(startTime);
        createRequest.setEndTime(endTime);
    }
    
    @Test
    void createReservation_ShouldCreateReservation_WhenValidRequest() {
        // Given
        when(parkingSlotService.getParkingSlotEntity(1L)).thenReturn(testSlot);
        when(reservationRepository.hasConflictingReservation(anyLong(), any(), any())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        
        // When
        ReservationResponse response = reservationService.createReservation(createRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("KA05MH1234", response.getVehicleNumber());
        assertEquals(VehicleType.FOUR_WHEELER, response.getVehicleType());
        verify(parkingSlotService).getParkingSlotEntity(1L);
        verify(reservationRepository).hasConflictingReservation(anyLong(), any(), any());
        verify(reservationRepository).save(any(Reservation.class));
    }
    
    @Test
    void createReservation_ShouldThrowException_WhenSlotHasConflict() {
        // Given
        when(parkingSlotService.getParkingSlotEntity(1L)).thenReturn(testSlot);
        when(reservationRepository.hasConflictingReservation(anyLong(), any(), any())).thenReturn(true);
        
        // When & Then
        assertThrows(BusinessRuleViolationException.class,
                    () -> reservationService.createReservation(createRequest));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
    
    @Test
    void createReservation_ShouldThrowException_WhenVehicleTypeMismatch() {
        // Given
        ParkingSlot twoWheelerSlot = new ParkingSlot("B1", testFloor, VehicleType.TWO_WHEELER);
        when(parkingSlotService.getParkingSlotEntity(1L)).thenReturn(twoWheelerSlot);
        when(reservationRepository.hasConflictingReservation(anyLong(), any(), any())).thenReturn(false);
        
        // When & Then
        assertThrows(BusinessRuleViolationException.class,
                    () -> reservationService.createReservation(createRequest));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
    
    @Test
    void createReservation_ShouldThrowException_WhenStartTimeAfterEndTime() {
        // Given
        createRequest.setStartTime(LocalDateTime.now().plusHours(2));
        createRequest.setEndTime(LocalDateTime.now().plusHours(1));
        
        // When & Then
        assertThrows(BusinessRuleViolationException.class,
                    () -> reservationService.createReservation(createRequest));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
    
    @Test
    void createReservation_ShouldThrowException_WhenDurationExceeds24Hours() {
        // Given
        createRequest.setStartTime(LocalDateTime.now().plusHours(1));
        createRequest.setEndTime(LocalDateTime.now().plusHours(26));
        
        // When & Then
        assertThrows(BusinessRuleViolationException.class,
                    () -> reservationService.createReservation(createRequest));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
    
    @Test
    void getReservationById_ShouldReturnReservation_WhenExists() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        
        // When
        ReservationResponse response = reservationService.getReservationById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("KA05MH1234", response.getVehicleNumber());
        verify(reservationRepository).findById(1L);
    }
    
    @Test
    void getReservationById_ShouldThrowException_WhenNotExists() {
        // Given
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class,
                    () -> reservationService.getReservationById(1L));
        verify(reservationRepository).findById(1L);
    }
    
    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        // Given
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(testReservation));
        
        // When
        List<ReservationResponse> responses = reservationService.getAllReservations();
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        verify(reservationRepository).findAll();
    }
    
    @Test
    void getActiveReservations_ShouldReturnActiveReservations() {
        // Given
        when(reservationRepository.findByStatus(ReservationStatus.ACTIVE))
                .thenReturn(Arrays.asList(testReservation));
        
        // When
        List<ReservationResponse> responses = reservationService.getActiveReservations();
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(ReservationStatus.ACTIVE, responses.get(0).getStatus());
        verify(reservationRepository).findByStatus(ReservationStatus.ACTIVE);
    }
    
    @Test
    void cancelReservation_ShouldCancelReservation_WhenExists() {
        // Given
        when(reservationRepository.findByIdAndStatus(1L, ReservationStatus.ACTIVE))
                .thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        
        // When
        ReservationResponse response = reservationService.cancelReservation(1L);
        
        // Then
        assertNotNull(response);
        verify(reservationRepository).findByIdAndStatus(1L, ReservationStatus.ACTIVE);
        verify(reservationRepository).save(any(Reservation.class));
    }
    
    @Test
    void cancelReservation_ShouldThrowException_WhenNotExists() {
        // Given
        when(reservationRepository.findByIdAndStatus(1L, ReservationStatus.ACTIVE))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class,
                    () -> reservationService.cancelReservation(1L));
        verify(reservationRepository).findByIdAndStatus(1L, ReservationStatus.ACTIVE);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}