package com.parkinglot.config;

import com.parkinglot.entity.Floor;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.model.SlotStatus;
import com.parkinglot.model.VehicleType;
import com.parkinglot.repository.FloorRepository;
import com.parkinglot.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataLoader implements CommandLineRunner {
    
    private final FloorRepository floorRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (floorRepository.count() == 0) {
            loadSampleData();
        }
    }
    
    private void loadSampleData() {
        log.info("Loading sample data...");
        
        // Create floors
        Floor groundFloor = new Floor(0, "Ground Floor");
        Floor firstFloor = new Floor(1, "First Floor");
        Floor secondFloor = new Floor(2, "Second Floor");
        
        floorRepository.save(groundFloor);
        floorRepository.save(firstFloor);
        floorRepository.save(secondFloor);
        
        // Create parking slots for ground floor
        createSlotsForFloor(groundFloor, "A", 10, VehicleType.FOUR_WHEELER);
        createSlotsForFloor(groundFloor, "B", 5, VehicleType.TWO_WHEELER);
        
        // Create parking slots for first floor
        createSlotsForFloor(firstFloor, "A", 15, VehicleType.FOUR_WHEELER);
        createSlotsForFloor(firstFloor, "B", 8, VehicleType.TWO_WHEELER);
        
        // Create parking slots for second floor
        createSlotsForFloor(secondFloor, "A", 20, VehicleType.FOUR_WHEELER);
        createSlotsForFloor(secondFloor, "B", 10, VehicleType.TWO_WHEELER);
        
        // Update floor total slots
        groundFloor.setTotalSlots(15);
        firstFloor.setTotalSlots(23);
        secondFloor.setTotalSlots(30);
        
        floorRepository.save(groundFloor);
        floorRepository.save(firstFloor);
        floorRepository.save(secondFloor);
        
        log.info("Sample data loaded successfully");
    }
    
    private void createSlotsForFloor(Floor floor, String sectionPrefix, int count, VehicleType vehicleType) {
        for (int i = 1; i <= count; i++) {
            String slotNumber = sectionPrefix + String.format("%02d", i);
            ParkingSlot slot = new ParkingSlot(slotNumber, floor, vehicleType);
            slot.setStatus(SlotStatus.AVAILABLE);
            parkingSlotRepository.save(slot);
        }
        log.info("Created {} {} slots in section {} for floor {}", 
                count, vehicleType, sectionPrefix, floor.getFloorName());
    }
}