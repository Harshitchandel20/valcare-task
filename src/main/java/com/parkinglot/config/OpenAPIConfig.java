package com.parkinglot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI parkingLotOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development server");
        
        Contact contact = new Contact();
        contact.setName("Parking Lot System");
        
        Info info = new Info()
                .title("Parking Lot Reservation API")
                .version("1.0.0")
                .description("REST API for managing parking lot reservations with multi-floor support")
                .contact(contact);
        
        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}