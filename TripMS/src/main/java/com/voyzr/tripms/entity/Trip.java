package com.voyzr.tripms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Trip JPA entity.
 * Fields as requested: id, name, descriptin, start date and status.
 */
@Entity
@Table(name = "trip")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name must not be blank")
    @Size(max = 255, message = "name must be at most 255 characters")
    @Column(nullable = false, length = 255)
    private String name;
    
    @Size(max = 1000, message = "description must be at most 1000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "startDate must not be null")
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull(message = "status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TripStatus status;
}
