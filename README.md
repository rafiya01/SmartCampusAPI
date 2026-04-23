**SMART CAMPUS MANAGEMENT API**

**MODULE: 5COSC022W Client-Server Architecture**

**1. API DESIGN AND ARCHITECTURAL OVERVIEW**

The Smart Campus API is a resource-oriented system built on the JAX-RS (Jersey) framework. It follows a decoupled architecture where the client and server communicate via standardized RESTful principles.

**1.1 RESOURCE HIERARCHY AND URI SCHEME**

The API uses a hierarchical URI structure to represent the physical containment of campus infrastructure.

•	Rooms act as top-level containers.

•	Sensors are associated with Rooms via unique identifiers.

•	SensorReadings are modeled as dependent sub-resources. They are accessed via the path /sensors/{id}/readings. This hierarchical nesting ensures that telemetry data is always contextually tied to a specific hardware sensor, maintaining referential integrity throughout the system state.

**1.2 THREAD-SAFE IN-MEMORY PERSISTENCE**

In accordance with the coursework constraints prohibiting external SQL databases, the system utilizes a custom DataStore class. To ensure the API can handle concurrent requests from multiple sensors or clients simultaneously, all data is stored in ConcurrentHashMaps. This provides bucket-level locking and prevents race conditions or data loss that would occur with standard HashMaps in a multi-threaded servlet environment.

**1.3 STATE SYNCHRONIZATION**

The API implements automatic state updates. When a new reading is POSTed to a sensor's reading collection, the system triggers a side-effect that updates the "currentValue" field on the parent Sensor object. This design allows clients to see the most recent status of a sensor via a simple GET request without the overhead of querying the entire historical log.

**2. PROJECT STRUCTURE AND TECHNICAL FILE MAP**

**2.1 SOURCE CODE DIRECTORIES**

•	**API Configuration:** src/main/java/com/mycompany/ SmartCampusAPI//SmartCampusApplication.java

•	**Resource Controllers:** src/main/java/com/mycompany/SmartCampusAPI/resources/

•	**Entity Models:** src/main/java/com/mycompany/ SmartCampusAPI/ /models/

•	**Persistence Logic:** src/main/java/com/mycompany/SmartCampusAPI/pusapi/data/DataStore.java

•	**Exception Handling:** src/main/java/com/mycompany/ SmartCampusAPI/ /exceptions/

•	**Logging and Filters**: src/main/java/com/mycompany/ SmartCampusAPI/ /filters/SmartCampusLoggingFilter.java

•	**Deployment Descriptor:** src/main/webapp/WEB-INF/web.xml

**3. BUILD AND LAUNCH INSTRUCTIONS**

1.	PREREQUISITES: Ensure Java JDK, Apache Maven, and Apache Tomcat are installed on the host system.
  
2.	BUILD PROCESS: Open the project in the NetBeans IDE. Right-click the project root and select "Clean and Build". Maven will resolve dependencies and generate a .war artifact in the /target directory.
   
3.	DEPLOYMENT: Right-click the project and select "Run". Assign Apache Tomcat 9.0 as the target deployment server.
   
4.	VERIFICATION: Once the server is initialized, the API base entry point is accessible at: http://localhost:8080/SmartCampusAPI/api/v1/

**4. SAMPLE CURL COMMANDS AND EXPECTED RESULTS**

**1.	DISCOVERY SERVICE**
Command: curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/

Result: A JSON object containing API versioning and HATEOAS links for rooms and sensors.

**2.	CREATE ROOM RESOURC**

**Command**: curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"R101\", \"name\":\"Computing Lab\"}"

**Result:** HTTP 201 Created. The body returns the JSON representation of the new room.

**3.	REGISTER SENSOR**

**Command:** curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"S1\", \"type\":\"CO2\", \"roomId\":\"R101\"}"

**Result:** HTTP 201 Created. If the RoomId R101 does not exist, the API returns 422 Unprocessable Entity.

**4.	FILTER SENSORS BY ATTRIBUTE**

**Command:** curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"

**Result**: HTTP 200 OK. Returns a JSON array containing only sensors of type CO2.

**5.	SUBMIT SENSOR READING**

**Command**: curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/S1/readings -H "Content-Type: application/json" -d "{\"value\": 500.0}"

**Result:** HTTP 201 Created. The historical log for S1 is updated, and the parent sensor's currentValue is synchronized to 500.0.

**5. COURSEWORK REPORT QUESTIONS**

**PART 1: SERVICE ARCHITECTURE**

**QUESTION:** Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this impacts data synchronization.

**ANSWER:** JAX-RS resource classes are request-scoped by default. The container creates a unique instance for every incoming HTTP request and discposes of it immediately after the response is sent. Since instance-level variables cannot persist data across multiple requests, I utilized static members within a central DataStore class to maintain state. To prevent race conditions inherent in multi-threaded servlet environments, I implemented ConcurrentHashMaps. This ensures thread-safe access and atomic updates, preventing data corruption when multiple clients attempt to modify the campus state simultaneously.

**QUESTION:** Why is the provision of Hypermedia (HATEOAS) considered a hallmark of advanced RESTful design?

**ANSWER:** HATEOAS represents the highest level of the Richardson Maturity Model. By including navigation links within JSON responses, the API becomes self-descriptive. This allows client developers to discover available resources and actions dynamically rather than relying on static external documentation. It decouples the client from the server URI structure; if the server-side paths are modified, the client continues to function seamlessly by following the links provided in the response body.

**PART 2: ROOM MANAGEMENT**

**QUESTION:** What are the implications of returning only IDs versus returning full room objects in a list?

**ANSWER:** Returning only IDs minimizes network payload size, which is critical for low-bandwidth mobile environments. However, it leads to high "chattiness," requiring the client to perform an additional request for every item in the list to display meaningful data. Returning full objects increases the initial data transfer but allows the client to populate the user interface with a single request, significantly reducing latency and server load caused by redundant calls.

**QUESTION:** Is the DELETE operation idempotent in your implementation?

**ANSWER:** Yes. The first DELETE request for a specific room ID successfully removes the resource and returns 204 No Content. Any subsequent identical requests will return 404 Not Found because the resource no longer exists. While the status codes differ, the final state of the server is identical after the first and any subsequent calls: the resource is removed. This satisfies the architectural requirement for idempotency.

**PART 3: SENSOR OPERATIONS**

**QUESTION:** Explain the consequences if a client sends data in a different format, such as text/plain, when the method consumes JSON.

**ANSWER:** This triggers a Content-Type mismatch handled by the JAX-RS runtime. Because the method is annotated with @Consumes(MediaType.APPLICATION_JSON), the server expects a specific header. If text/plain is provided, the runtime intercepts the request and returns an HTTP 415 Unsupported Media Type error. The business logic is never executed, ensuring the system remains robust against incompatible data formats.

**QUESTION:** Contrast @QueryParam with an alternative design where the type is part of the URL path.

**ANSWER:** URL path parameters are intended to identify a specific, unique resource by its identity. Query parameters are intended for modifiers such as filtering, sorting, or searching within a collection. Using @QueryParam for sensor types is superior because it allows for optional and combinatory filters without changing the core resource path, keeping the URI structure clean and logical.

**PART 4: DEEP NESTING**

**QUESTION:** Discuss the architectural benefits of the Sub-Resource Locator pattern.

**ANSWER:** The Sub-Resource Locator pattern promotes a modular separation of concerns. By delegating the logic for readings to a dedicated SensorReadingResource class, I avoid the creation of a "God Class" that handles too many responsibilities. This enhances code maintainability and readability, as the logic for sensors and their respective readings is isolated into manageable, specialized components.
PART 5: ROBUSTNESS AND SECURITY

**QUESTION:** Why is HTTP 422 more semantically accurate than 404 when a reference inside a JSON payload is missing?

**ANSWER:** A 404 error suggests that the requested URL path does not exist. However, if the URL is correct but a value inside the JSON (such as a Room ID) refers to a non-existent entity, the request is well-formed but logically unprocessable. HTTP 422 (Unprocessable Entity) accurately informs the developer that the connection was successful, but the data provided violates business logic constraints.

**QUESTION:** What are the risks of exposing Java stack traces to external consumers?

**ANSWER:** Exposing stack traces is a form of information leakage. It reveals internal server file paths, framework versions, and the specific names of classes and methods. An attacker can use this information during the reconnaissance phase to identify known vulnerabilities in the specific versions of the software stack (e.g., Tomcat or Jersey) and plan targeted exploits against the system architecture.

**QUESTION:** Why use JAX-RS filters for logging instead of manual statements in every method?

**ANSWER:** Filters allow for the implementation of cross-cutting concerns in a centralized manner. Manual logging is prone to human error and results in significant code duplication. By using ContainerRequestFilter and ContainerResponseFilter, the API ensures 100% observability coverage. Every incoming request and outgoing response is automatically logged, providing a reliable audit trail for debugging and monitoring without cluttering the core business logic.

