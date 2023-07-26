package org.acme.entity;

import java.sql.Timestamp;

import org.acme.resources.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private Status status;

    private Timestamp createdAt;

    private Timestamp updatedAt;  
}