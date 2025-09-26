package com.parkinglot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "floors")
@Data
@EqualsAndHashCode(callSuper = false)
public class Floor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Positive
    @Column(name = "floor_number", unique = true, nullable = false)
    private Integer floorNumber;
    
    @NotBlank
    @Column(name = "floor_name", nullable = false)
    private String floorName;
    
    @Column(name = "total_slots")
    private Integer totalSlots = 0;
    
    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ParkingSlot> parkingSlots;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Floor() {}
    
    public Floor(Integer floorNumber, String floorName) {
        this.floorNumber = floorNumber;
        this.floorName = floorName;
    }
}