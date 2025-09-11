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


## 3. Milestones & Time Frames
- **Time Frame:** 4 Weeks  
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


## 4. Database Design
Each service will use its own database for storing and managing data related to its responsibilities.  

- **Auth Service DB** - manages user accounts, credentials, and roles.
<img src="assets/auth.jpg" style="width:50%;"/>

- **Device Service DB** - manages all device records, status, and assignment information.
<img src="assets/device.jpg" style="width:50%;"/>

- **Request Service DB** - manages device requests, approvals, rejections, and workflow history.  
- **Report Service DB** - stores aggregated data and reporting snapshots.  


## 5. Technical Specifications
- **Architecture:** Microservices architecture with independent services (Auth, Device, Request, Report).  
- **Service Communication:** REST APIs (optionally with an API Gateway).  
- **Containerization:** Each service runs in its own Docker container.  
- **Persistence:** Shared PostgreSQL (or per-service schema separation).  
- **Scalability:** Services can be scaled independently.  
- **Frameworks:** Java / Spring Boot for each service.  
- **Authentication:** Centralized Auth service with JWT tokens.  
