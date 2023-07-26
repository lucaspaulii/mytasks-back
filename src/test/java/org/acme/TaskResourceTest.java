package org.acme;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.acme.DTOs.TaskInsertDTO;
import org.acme.repository.TaskRepository;
import org.acme.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
public class TaskResourceTest {

    @Inject
    TaskRepository taskRepository;
    TaskService taskService;

    @AfterEach
    @Transactional
    public void cleanupDatabase() {
        taskRepository.deleteAll();
    }

    @Test
    @TestTransaction
    public void testAddTaskEndpoint() {

        TaskInsertDTO validBody = new TaskInsertDTO();
        validBody.setTitle("Test");
        validBody.setDescription("testing post endpoint");

        //test with empty body
        given()
            .body("{}")
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then()
                .statusCode(400);

        //test with empty title (invalid)
        given()
            .body("{\"title\": \"\",\"description\":\"testing post endpoint\"}")
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then()
                .statusCode(400);

        //test with empty description (invalid)
        given()
            .body("{\"title\": \"Test\",\"description\":\"\"}")
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then()
                .statusCode(400);
        
        //endpoint ignores unsolicited values
        given()
            .body("{\"title\": \"Test\",\"description\":\"testing post endpoint\",\"createdAt\":\"2012-01-01 00:00:00\"}")
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then()
                .statusCode(201);
        
        given()
          .when().get("/api/task")
          .then()
             .statusCode(200)
             .body("[0].createdAt", not(equalTo("2012-01-01 00:00:00")));


        //test with valid body
        given()
            .body(validBody)
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then()
                .statusCode(201);

         given()
          .when().get("/api/task")
          .then()
             .statusCode(200)
             .body("[0].title", is("Test"))
             .body("[0].description", is("testing post endpoint"));
    }

    @Test
    @TestTransaction
    public void testGetAllEndpoint() {
        
        //success case with empty body (no values inside the db)
        given()
          .when().get("/api/task")
          .then()
             .statusCode(200)
             .body(is("[]"));
        
        //success case with ordered results (ordered by createdAt)

        //create tasks for testing purposes;

        TaskInsertDTO validBody1 = new TaskInsertDTO();
        validBody1.setTitle("Test Older");
        validBody1.setDescription("testing get endpoint");

        given()
            .body(validBody1)
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then();

        TaskInsertDTO validBody2 = new TaskInsertDTO();
        validBody2.setTitle("Test Newer");
        validBody2.setDescription("testing get endpoint");

        given()
            .body(validBody2)
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then();
       

        given()
          .when().get("/api/task")
          .then()
             .statusCode(200)
             .body("$.size()", is(2))
             .body("[0].title", is("Test Newer"))
             .body("[1].title", is("Test Older"));
    }

    
    @Test
    @TestTransaction
    public void testConcludeTaskEndpoint() {

        TaskInsertDTO validBody = new TaskInsertDTO();
        validBody.setTitle("Test");
        validBody.setDescription("testing conclude endpoint");

        given()
            .body(validBody)
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then();
        
        Response response = given()
        .when()
        .get("/api/task");

        Integer validId = response.body().jsonPath().get("[0].id");
        Long invalidId = -1L;
        //test with invalid id
        given()
            .pathParam("id", invalidId)
            .when().put("/api/task/conclude/{id}")
            .then()
                .statusCode(404);

        //test with valid id
        given()
            .pathParam("id", validId)
            .when().put("/api/task/conclude/{id}")
            .then()
                .statusCode(200);

        given()
            .pathParam("id", validId)
            .when().get("/api/task/{id}")
            .then()
            .statusCode(200)
            .body("status", is("CONCLUDED"));
    }

    @Test
    @TestTransaction
    public void testEditTaskEndpoint() {

        TaskInsertDTO validBody = new TaskInsertDTO();
        validBody.setTitle("Test");
        validBody.setDescription("testing conclude endpoint");

        given()
            .body(validBody)
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then();
        
        Response response = given()
        .when()
        .get("/api/task");

        Integer validId = response.body().jsonPath().get("[0].id");
        Long invalidId = -1L;

        TaskInsertDTO editBody = new TaskInsertDTO();
        editBody.setTitle("Edit Test");
        editBody.setDescription("testing edit endpoint");

        //test invalid id
        given()
            .body(editBody)
            .header("Content-Type", "application/json")
            .pathParam("id", invalidId)
            .when().put("/api/task/edit/{id}")
            .then()
                .statusCode(404);

        //test with empty body
        given()
            .body("{}")
            .header("Content-Type", "application/json")
            .pathParam("id", validId)
            .when().put("/api/task/edit/{id}")
            .then()
                .statusCode(400);

        //test with empty title (invalid)
        given()
            .body("{\"title\": \"\",\"description\":\"testing edit endpoint\"}")
            .header("Content-Type", "application/json")
            .pathParam("id", validId)
            .when().put("/api/task/edit/{id}")
            .then()
                .statusCode(400);

        //test with empty description (invalid)
        given()
            .body("{\"title\": \"Edited Test\",\"description\":\"\"}")
            .header("Content-Type", "application/json")
            .pathParam("id", validId)
            .when().put("/api/task/edit/{id}")
            .then()
                .statusCode(400);

        //endpoint ignores unsolicited values
        given()
            .body("{\"title\": \"Edited Test\",\"description\":\"testing edit endpoint\",\"createdAt\":\"2012-01-01 00:00:00\"}")
            .header("Content-Type", "application/json")
            .pathParam("id", validId)
            .when().put("/api/task/edit/{id}")
            .then()
                .statusCode(200);

        given()
          .when().get("/api/task")
          .then()
             .statusCode(200)
             .body("[0].createdAt", not(equalTo("2012-01-01 00:00:00")));

        //test valid id
        given()
            .body("{\"title\": \"Edited Test\",\"description\":\"testing edit endpoint\"}")
            .header("Content-Type", "application/json")
            .pathParam("id", validId)
            .when().put("/api/task/edit/{id}")
            .then()
                .statusCode(200);

        given()
            .pathParam("id", validId)
            .when().get("/api/task/{id}")
            .then()
            .statusCode(200)
            .body("title", is("Edited Test"))
            .body("description", is("testing edit endpoint"));
    }

    @Test
    @TestTransaction
    public void testDeleteTaskEndpoint() {

        //inserting data into the database for testing purposes;

        TaskInsertDTO validBody = new TaskInsertDTO();
        validBody.setTitle("Test");
        validBody.setDescription("testing conclude endpoint");

        given()
            .body(validBody)
            .header("Content-Type", "application/json")
            .when().post("/api/task")
            .then();
        
        Response response = given()
        .when()
        .get("/api/task");

        Integer validId = response.body().jsonPath().get("[0].id");
        Long invalidId = -1L;

        //test with invalid id
        given()
            .pathParam("id", invalidId)
            .when().delete("/api/task/{id}")
            .then()
                .statusCode(404);

        //test with valid id
        given()
            .pathParam("id", validId)
            .when().delete("/api/task/{id}")
            .then()
                .statusCode(204);

        given()
            .pathParam("id", validId)
            .when().get("/api/task/{id}")
            .then()
            .statusCode(404);
    }
}