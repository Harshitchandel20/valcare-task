package com.parkinglot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parkinglot.dto.FloorCreateRequest;
import com.parkinglot.dto.FloorResponse;
import com.parkinglot.service.FloorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FloorController.class)
class FloorControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private FloorService floorService;
    
    private ObjectMapper objectMapper;
    private FloorResponse floorResponse;
    private FloorCreateRequest createRequest;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        floorResponse = new FloorResponse();
        floorResponse.setId(1L);
        floorResponse.setFloorNumber(1);
        floorResponse.setFloorName("Ground Floor");
        floorResponse.setTotalSlots(0);
        floorResponse.setCreatedAt(LocalDateTime.now());
        floorResponse.setUpdatedAt(LocalDateTime.now());
        
        createRequest = new FloorCreateRequest();
        createRequest.setFloorNumber(1);
        createRequest.setFloorName("Ground Floor");
    }
    
    @Test
    void createFloor_ShouldReturn201_WhenValidRequest() throws Exception {
        // Given
        when(floorService.createFloor(any(FloorCreateRequest.class))).thenReturn(floorResponse);
        
        // When & Then
        mockMvc.perform(post("/api/floors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.floorNumber").value(1))
                .andExpect(jsonPath("$.floorName").value("Ground Floor"));
    }
    
    @Test
    void createFloor_ShouldReturn400_WhenInvalidRequest() throws Exception {
        // Given
        FloorCreateRequest invalidRequest = new FloorCreateRequest();
        invalidRequest.setFloorNumber(-1); // Invalid
        invalidRequest.setFloorName(""); // Invalid
        
        // When & Then
        mockMvc.perform(post("/api/floors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getAllFloors_ShouldReturn200_WhenFloorsExist() throws Exception {
        // Given
        List<FloorResponse> floors = Arrays.asList(floorResponse);
        when(floorService.getAllFloors()).thenReturn(floors);
        
        // When & Then
        mockMvc.perform(get("/api/floors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].floorNumber").value(1));
    }
}