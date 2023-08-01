# Task Handler

API RESTful created using Java and Quarkus.

Using Hibernate/Panache to persist data (currently using H2 in memory to simplify the application use in this assignment)

## Running the application in dev mode

To run the application:
```
mvn install
mvn compile quarkus:dev
```

The application will run on your http://localhost:9090 by default.

## Testing the application in dev mode

After running the application in dev mode, press 'r' to resume testing.

## Endpoints

### Task Entity
```
{
    id: Long,
    title: String,
    description: String,
    createdAt: Timestamp,
    updatedAt: Timestamp,
    Status: PENDING / CONCLUDED
}
```

### @GET /api/task 
Will return an array of Tasks ordered from newer to older

### @GET /api/task/{id}
Will return a Task found with the given id or throw a 404 error

### @POST /api/task
Receives a JSON Object in the following format:
```
    {
        "title": "task title here",
        "description": "task description here"
    }
```

and then create a Task in the database, auto generating the id, createdAt, updatedAt and status values.

### @PUT /api/task/conclude/{id}
Updates the Task with the given id, changing its Status to CONCLUDED and updating its updatedAt field.

### @PUT /api/task/edit/{id}
Receives a JSON Object in the following format:
```
    {
        "title": "task title here",
        "description": "task description here"
    }
```
and then updates the Task with the given id, updating its title, description and updatedAt fields.

### @DELETE /api/task/{id}
Will delete the Task with given Id, returning 204, if not found will throw a 404 error
