package org.acme.controller;

import org.acme.service.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.acme.DTOs.TaskInsertDTO;
import org.acme.entity.Task;
import org.acme.exception.TaskNotFoundException;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/task")
public class TaksController {

    @Inject TaskService taskService;

    @GET
    public Response getTasks(){
        List<Task> tasks = new ArrayList<>();
        try {
            tasks = taskService.findAllTasks();
            return Response.ok(tasks).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    } 

    @GET
    @Path("/{id}")
    public Response getTaskById(@PathParam("id") Long id) {
        try {
            Task task = taskService.findById(id);
            return Response.ok(task).build();
        } catch (TaskNotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND)
                .entity("Task not found with ID: " + id)
                .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTask(@Valid TaskInsertDTO taskInsertDTO) {
        try {

            taskService.createTask(taskInsertDTO);
            return Response.status(Response.Status.CREATED).build();

        } catch (ConstraintViolationException ex) {

            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            StringBuilder errorMessage = new StringBuilder("Validation errors:");
            for (ConstraintViolation<?> violation : violations) {
                errorMessage.append(" ").append(violation.getMessage());
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage.toString()).build();

        } catch (Exception e) {

            e.printStackTrace();
            return Response.serverError().build();

        }
    }
    
    @PUT
    @Path("/conclude/{id}")
    public Response concludeTask(@PathParam("id") Long id) {
        try {
            taskService.concludeTask(id);
            return Response.ok().build();
        } catch (TaskNotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND)
                .entity("Task not found with ID: " + id)
                .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }    
    }

    @PUT
    @Path("/edit/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editTask(@Valid TaskInsertDTO taskInsertDTO, @PathParam("id") Long id) {
        try {
            taskService.editTask(taskInsertDTO, id);
            return Response.ok().build();
        } catch (TaskNotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND)
                .entity("Task not found with ID: " + id)
                .build();

        } catch (ConstraintViolationException ex) {

            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            StringBuilder errorMessage = new StringBuilder("Validation errors:");
            for (ConstraintViolation<?> violation : violations) {
                errorMessage.append(" ").append(violation.getMessage());
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage.toString()).build();

        } catch (Exception e) {

            e.printStackTrace();
            return Response.serverError().build();
            
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTask(@PathParam("id") Long id) {
        try {
            taskService.deleteTask(id);
            return Response.noContent().build();
        } catch (TaskNotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND)
                .entity("Task not found with ID: " + id)
                .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }     
    }
}
