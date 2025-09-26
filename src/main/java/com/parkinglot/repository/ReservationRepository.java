package com.parkinglot.repository;

import com.parkinglot.entity.Reservation;
import com.parkinglot.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByStatus(ReservationStatus status);
    
    List<Reservation> findByParkingSlotId(Long parkingSlotId);
    
    Optional<Reservation> findByIdAndStatus(Long id, ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.parkingSlot.id = :slotId AND r.status = 'ACTIVE' AND " +
           "((r.startTime <= :endTime AND r.endTime >= :startTime))")
    List<Reservation> findConflictingReservations(@Param("slotId") Long slotId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.parkingSlot.id = :slotId AND r.status = 'ACTIVE' AND " +
           "((r.startTime <= :endTime AND r.endTime >= :startTime))")
    boolean hasConflictingReservation(@Param("slotId") Long slotId, 
                                      @Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);
}