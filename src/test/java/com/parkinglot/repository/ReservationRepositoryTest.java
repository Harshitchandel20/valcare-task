package com.parkinglot.repository;

import com.parkinglot.entity.Floor;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.Reservation;
import com.parkinglot.model.ReservationStatus;
import com.parkinglot.model.SlotStatus;
import com.parkinglot.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    private Floor testFloor;
    private ParkingSlot testSlot;
    private Reservation testReservation;
    
    @BeforeEach
    void setUp() {
        testFloor = new Floor(1, "Ground Floor");
        entityManager.persistAndFlush(testFloor);
        
        testSlot = new ParkingSlot("A1", testFloor, VehicleType.FOUR_WHEELER);
        testSlot.setStatus(SlotStatus.AVAILABLE);
        entityManager.persistAndFlush(testSlot);
        
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        
        testReservation = new Reservation(testSlot, "KA05MH1234", VehicleType.FOUR_WHEELER, startTime, endTime);
        testReservation.setStatus(ReservationStatus.ACTIVE);
    }
    
    @Test
    void findByStatus_ShouldReturnReservationsWithGivenStatus() {
        // Given
        entityManager.persistAndFlush(testReservation);
        
        // When
        List<Reservation> activeReservations = reservationRepository.findByStatus(ReservationStatus.ACTIVE);
        List<Reservation> cancelledReservations = reservationRepository.findByStatus(ReservationStatus.CANCELLED);
        
        // Then
        assertEquals(1, activeReservations.size());
        assertEquals(0, cancelledReservations.size());
        assertEquals(testReservation.getVehicleNumber(), activeReservations.get(0).getVehicleNumber());
    }
    
    @Test
    void hasConflictingReservation_ShouldReturnTrue_WhenConflictExists() {
        // Given
        entityManager.persistAndFlush(testReservation);
        
        LocalDateTime conflictStart = testReservation.getStartTime().plusMinutes(30);
        LocalDateTime conflictEnd = testReservation.getEndTime().plusMinutes(30);
        
        // When
        boolean hasConflict = reservationRepository.hasConflictingReservation(
                testSlot.getId(), conflictStart, conflictEnd);
        
        // Then
        assertTrue(hasConflict);
    }
    
    @Test
    void hasConflictingReservation_ShouldReturnFalse_WhenNoConflict() {
        // Given
        entityManager.persistAndFlush(testReservation);
        
        LocalDateTime noConflictStart = testReservation.getEndTime().plusHours(1);
        LocalDateTime noConflictEnd = noConflictStart.plusHours(1);
        
        // When
        boolean hasConflict = reservationRepository.hasConflictingReservation(
                testSlot.getId(), noConflictStart, noConflictEnd);
        
        // Then
        assertFalse(hasConflict);
    }
    
    @Test
    void findConflictingReservations_ShouldReturnConflicts() {
        // Given
        entityManager.persistAndFlush(testReservation);
        
        LocalDateTime conflictStart = testReservation.getStartTime().plusMinutes(30);
        LocalDateTime conflictEnd = testReservation.getEndTime().plusMinutes(30);
        
        // When
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                testSlot.getId(), conflictStart, conflictEnd);
        
        // Then
        assertEquals(1, conflicts.size());
        assertEquals(testReservation.getId(), conflicts.get(0).getId());
    }
}