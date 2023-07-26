package org.acme.repository;

import org.acme.entity.Task;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task>{  
}
