# Equipment Management System

## 1. Objective
To design, develop, and deliver a **microservices-based RESTful API** that enables company-wide device management. The system is decomposed into independent services for authentication, device CRUD operations, request/approval workflows, and reporting, each running in its own container.


## 2. Scope of Work
The project will be composed of the following microservices:

- **Auth Service** - handles user login, JWT generation, and role-based access.  
- **Device Service** - manages CRUD operations on devices.  
- **Request Service** - manages device requests and approval/rejection workflows.  
- **Report Service** - generates monthly reports for Admins.  

> All services will communicate via REST APIs.

## 3. API Endpoints

1. **Auth Service**

- **Login**
    - **Name:** `/login`  
    - **Endpoint:** `/auth/login`  
    - **Method:** `POST`  
    - **Description:** Authenticates a user and returns a JWT token on success.  
    - **Request Body:**
    ```json
    {
        "email": "admin@example.com",
        "password": "12345678"
    }
    ```
    - **Responses**:  
    - **Success**:  
    ```json
    {
        "status": "success",
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
    ```  
    - **Fail**:  
    - **Invalid input** - Returned when email or password is null 
     ```json
    {
        "status": "error",
        "statusCode": 1001,
        "message": "Email and password are required"
    }
     ```

    - **Invalid info** - Returned when the input email or password is incorrect
     ```json
    { 
        "status": "error",
        "statusCode": 1002,
        "message": "Email or password is incorrect"
    }
     ```

2. **Device Service**

- **Add Device**
    - **Name:** `/addDevice`  
    - **Endpoint:** `/device`  
    - **Method:** `POST`  
    - **Description:** Adds a new device to the system (admin/IT only).
    - **Request Body:**
    ```json
    {
        "name": "Laptop Dell XPS 15",
        "type": "Laptop"
    }
    ```
    - **Responses**:  
    - **Success**:  
    ```json
    {
        "status": "success",
        "id": 16
    }
    ```  
    - **Fail**:  
    - **Invalid token** - Returned when the JWT token is missing, invalid, or expired
     ```json
    {
        "status": "error",
        "statusCode": 1004,
        "message": "Token is invalid or expired"
    }
     ```

    - **Unauthorized** - Returned when the user's role does not have permission to perform the requested operation
     ```json
    {
        "status": "error",
        "statusCode": 1005,
        "message": "Insufficient permissions"
    }
     ``` 

    - **Invalid input** - Returned when one or more fields of request body are blank or null
     ```json
    {
        "status": "error",
        "statusCode": 1003,
        "message": "Missing required fields"
    }
     ```

     - **Invalid type** - Returned when the input type is not found or inaccessible
     ```json
    {
        "status": "error",
        "statusCode": 1006,
        "message": "Device type not found or inaccessible"
    }
     ```

- **Update Device**
    - **Name:** `/updateDevice`  
    - **Endpoint:** `/device/{id}`  
    - **Method:** `PUT`  
    - **Description:** Update a device in the system (admin/IT only).
    - **Request Body:**
    ```json
    {
        "name": "Laptop Dell XPS 15",
        "type": "Laptop"
    }
    ```
    - **Responses**:  
    - **Success**:  
    ```json
    {
        "status": "success",
        "id": 16
    }
    ```  
    - **Fail**:  
    - **Invalid token** - Returned when the JWT token is missing, invalid, or expired
     ```json
    {
        "status": "error",
        "statusCode": 1004,
        "message": "Token is invalid or expired"
    }
     ```

    - **Unauthorized** - Returned when the user's role does not have permission to perform the requested operation
     ```json
    {
        "status": "error",
        "statusCode": 1005,
        "message": "Insufficient permissions"
    }
     ``` 

    - **Not found** - Returned when device ID is not found
     ```json
    {
        "status": "error",
        "statusCode": 1007,
        "message": "Device not found"
    }
     ```

    - **Invalid input** - Returned when one or more fields of request body are blank or null
     ```json
    {
        "status": "error",
        "statusCode": 1003,
        "message": "Missing required fields"
    }
     ```

     - **Invalid type** - Returned when the input type is not found or inaccessible
     ```json
    {
        "status": "error",
        "statusCode": 1006,
        "message": "Device type not found or inaccessible"
    }
     ```

- **View Device**
    - **Name:** `/viewDevice`  
    - **Endpoint:** `/device/{id}`  
    - **Method:** `GET`  
    - **Description:** View a device in the system (admin/IT only).
    - **Responses**:  
    - **Success**:  
    ```json
    {
        "status": "success",
        "device": {
            "id": 16,
            "uuid": "d1e8f080-9bfc-4b9b-a65f-2c5a04a5616d",
            "name": "Laptop Dell XPS 15",
            "type": "Laptop",
            "status": "AVAILABLE",
            "assignedTo": null
        }
    }
    ```  
    - **Fail**:  
    - **Invalid token** - Returned when the JWT token is missing, invalid, or expired
     ```json
    {
        "status": "error",
        "statusCode": 1004,
        "message": "Token is invalid or expired"
    }
     ```

    - **Unauthorized** - Returned when the user's role does not have permission to perform the requested operation
     ```json
    {
        "status": "error",
        "statusCode": 1005,
        "message": "Insufficient permissions"
    }
     ``` 

    - **Not found** - Returned when device ID is not found
     ```json
    {
        "status": "error",
        "statusCode": 1007,
        "message": "Device not found"
    }
     ```

    - **Inaccessible** - Returned when the user does not have permission to access the specified device
     ```json
    {
        "status": "error",
        "statusCode": 1008,
        "message": "Device inaccessible"
    }
     ```

- **Search devices**
    - **Name:** `/searchDevices`  
    - **Endpoint:** `/device/search`  
    - **Method:** `GET`  
    - **Description:** Search devices based on device name or type.
    - **Responses**:  
    - **Success**:  
    ```json
    {
        "status": "success",
        "results": [

            {
                "uuid": "745571f6-d52b-49ef-88cb-db0607853107",
                "name": "Laptop Dell XPS 30",
                "type": "Laptop",
                "status": "AVAILABLE"
            }
        ]
    }
    ```  
    - **Fail**:  
    - **Invalid token** - Returned when the JWT token is missing, invalid, or expired
     ```json
    {
        "status": "error",
        "statusCode": 1004,
        "message": "Token is invalid or expired"
    }
     ```

- **Decommission device**
    - **Name:** `/decommissionDevice`  
    - **Endpoint:** `/device/{id}`  
    - **Method:** `DELETE`  
    - **Description:** Update a device's status to be 'DECOMMISSION' in the system (admin/IT only).
    - **Responses**:  
    - **Success**:  
    ```json
    {
        "status": "success",
        "id": 16
    }
    ```  
    - **Fail**:  
    - **Invalid token** - Returned when the JWT token is missing, invalid, or expired
     ```json
    {
        "status": "error",
        "statusCode": 1004,
        "message": "Token is invalid or expired"
    }
     ```

    - **Unauthorized** - Returned when the user's role does not have permission to perform the requested operation
     ```json
    {
        "status": "error",
        "statusCode": 1005,
        "message": "Insufficient permissions"
    }
     ``` 

    - **Not found** - Returned when device ID is not found
     ```json
    {
        "status": "error",
        "statusCode": 1007,
        "message": "Device not found"
    }
     ```

    - **Inaccessible** - Returned when the user does not have permission to access the specified device
     ```json
    {
        "status": "error",
        "statusCode": 1008,
        "message": "Device inaccessible"
    }
     ```
     
## 4. Milestones & Time Frames
- **Time Frame:** 4 weeks  
- **Start Date:** 8 September, 2025  

| Date Range | Milestone | Description |
|------------|-----------|-------------|
| 08/09 - 09/09 | Project Setup and Planning | Define microservice boundaries, create repositories |
| 10/09 - 12/09 | Auth Service | Build authentication & authorization service with JWT. |
| 13/09 - 16/09 | Device Service | Implement device CRUD microservice, expose REST endpoints. |
| 17/09 - 20/09 | Request Service | Implement request handling microservice for employees and IT/Admin approvals. |
| 21/09 - 23/09 | Report Service | Build reporting microservice for monthly device overview. |
| 24/09 - 26/09 | Service Integration | Test inter-service communication, validate role-based flows. |
| 27/09 - 30/09 | System Testing | End-to-end testing across all microservices. |
| 01/10 - 03/10 | API Documentation | Document APIs of each service and prepare Postman collection. |
| 04/10 - 05/10 | Deployment and Review | Deploy multi-service system with Docker Compose. |


## 5. Database Design
Each service will use its own database for storing and managing data related to its responsibilities.  

- **Auth Service DB** - manages user accounts, credentials, and roles.
<img src="assets/auth.jpg" style="width:50%;"/>

> user_role: ('ADMIN', 'EMPLOYEE', 'IT')

- **Device Service DB** - manages all device records, status, and assignment information.
<img src="assets/device.jpg" style="width:100%;"/>

  > device_status: ('AVAILABLE', 'ASSIGNED', 'MAINTENANCE', 'DECOMMISSIONED')

  > device_category: ('GENERAL', 'NETWORK')

- **Request Service DB** - manages device requests, approvals, rejections, and workflow history.  
- **Report Service DB** - stores aggregated data and reporting snapshots.  


## 6. Technical Specifications
- **Architecture:** Microservices architecture with independent services (Auth, Device, Request, Report).  
- **Service Communication:** REST APIs (optionally with an API Gateway).  
- **Containerization:** Each service runs in its own Docker container.  
- **Persistence:** Shared PostgreSQL (or per-service schema separation).  
- **Scalability:** Services can be scaled independently.  
- **Frameworks:** Java / Spring Boot for each service.  
- **Authentication:** Centralized Auth service with JWT tokens.  
