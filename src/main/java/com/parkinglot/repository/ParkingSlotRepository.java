package com.parkinglot.repository;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.model.SlotStatus;
import com.parkinglot.model.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    
    List<ParkingSlot> findByFloorId(Long floorId);
    
    Optional<ParkingSlot> findByFloorIdAndSlotNumber(Long floorId, String slotNumber);
    
    boolean existsByFloorIdAndSlotNumber(Long floorId, String slotNumber);
    
    List<ParkingSlot> findByStatus(SlotStatus status);
    
    Page<ParkingSlot> findByStatus(SlotStatus status, Pageable pageable);
    
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.id NOT IN " +
           "(SELECT r.parkingSlot.id FROM Reservation r WHERE r.status = 'ACTIVE' AND " +
           "((r.startTime <= :endTime AND r.endTime >= :startTime)))")
    List<ParkingSlot> findAvailableSlots(@Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.vehicleType = :vehicleType AND ps.id NOT IN " +
           "(SELECT r.parkingSlot.id FROM Reservation r WHERE r.status = 'ACTIVE' AND " +
           "((r.startTime <= :endTime AND r.endTime >= :startTime)))")
    Page<ParkingSlot> findAvailableSlotsByVehicleType(@Param("vehicleType") VehicleType vehicleType,
                                                       @Param("startTime") LocalDateTime startTime, 
                                                       @Param("endTime") LocalDateTime endTime,
                                                       Pageable pageable);
}