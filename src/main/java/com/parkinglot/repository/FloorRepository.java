package com.parkinglot.repository;

import com.parkinglot.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    
    Optional<Floor> findByFloorNumber(Integer floorNumber);
    
    boolean existsByFloorNumber(Integer floorNumber);
    
    @Query("SELECT f FROM Floor f LEFT JOIN FETCH f.parkingSlots WHERE f.id = :id")
    Optional<Floor> findByIdWithSlots(Long id);
}