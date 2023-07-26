package org.acme.service;

import java.util.List;
import java.sql.Timestamp;

import org.acme.DTOs.TaskInsertDTO;
import org.acme.entity.Task;
import org.acme.exception.TaskNotFoundException;
import org.acme.repository.TaskRepository;
import org.acme.resources.Status;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TaskService {
    
    @Inject
    TaskRepository taskRepository;

    public List<Task> findAllTasks(){
        return taskRepository.findAll(Sort.by("createdAt").descending()).list();
    }

    public Task findById(Long id){
        Task task = taskRepository.findById(id);
         if (task == null) {
            throw new TaskNotFoundException("Task not found");
        }
        return task;
    }

    @Transactional
    public void createTask(TaskInsertDTO taskInsertDTO){

        Task task = new Task();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        task.setTitle(taskInsertDTO.getTitle());
        task.setDescription(taskInsertDTO.getDescription());
        task.setStatus(Status.PENDING);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        taskRepository.persist(task);
    }

    @Transactional
    public void concludeTask(Long id){
        Task task = this.findById(id);
        if (task == null) {
            throw new TaskNotFoundException("Task not found");
        }
        Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
        Status status = Status.CONCLUDED;

        task.setStatus(status);
        task.setUpdatedAt(updatedAt);
        
        taskRepository.getEntityManager().merge(task);
    }

    @Transactional
    public void editTask(TaskInsertDTO taskInsertDTO, Long id){
        Task task = this.findById(id);
         if (task == null) {
            throw new TaskNotFoundException("Task not found");
        }
        Timestamp updatedAt = new Timestamp(System.currentTimeMillis());

        task.setTitle(taskInsertDTO.getTitle());
        task.setDescription(taskInsertDTO.getDescription());
        task.setUpdatedAt(updatedAt);

        taskRepository.getEntityManager().merge(task);
    }

    @Transactional
    public void deleteTask(Long id){
        Task task = this.findById(id);
         if (task == null) {
            throw new TaskNotFoundException("Task not found");
        }
        taskRepository.delete("where id = :id", Parameters.with("id", id));
    }
}
