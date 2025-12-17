package com.voyzr.tripms.dto;

import com.voyzr.tripms.entity.TripStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {
    private Long id;
    @NotBlank(message = "name must not be blank")
    @Size(max = 255, message = "name must be at most 255 characters")
    private String name;
    @Size(max = 1000, message = "description must be at most 1000 characters")
    private String description;
    @NotNull(message = "startDate must not be null")
    private LocalDate startDate;
    @NotNull(message = "status must not be null")
    private TripStatus status;
}
