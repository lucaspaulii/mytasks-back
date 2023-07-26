package org.acme.DTOs;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskInsertDTO {

    @NotBlank(message = "Title may not be blank")
    private String title;

    @NotBlank(message = "Description may not be blank")
    private String description;
    
}