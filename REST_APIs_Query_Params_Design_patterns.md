# REST APIs, Query Parameters, and Software Design Patterns

_A Complete Study Guide & Knowledge Dump for Senior Engineers_

---

# PART 1 — REST APIs

### 1. What is an API?

**What API means:** Application Programming Interface.
**API vs application:** An application is the complete software product (e.g., the Uber app on your phone). The API is the invisible bridge that allows the Uber app to talk to the Uber servers.
**API vs backend:** The backend is the entire server-side system (database, business logic, background workers). The API is _only the entry point_ (the doors/windows) to that backend.
**API vs endpoint:** An API is the entire collection of available communication paths. An endpoint is one specific path (e.g., `GET /users`).
**How frontend and backend communicate:** The frontend sends an HTTP Request (containing headers, a URL, and a body). The backend processes it and returns an HTTP Response (containing a status code, headers, and a body).
**Real-world analogy:** Imagine a restaurant. You (the client) look at the menu (the API documentation). You give your order to the waiter (the API). The waiter takes the order to the kitchen (the backend). The kitchen prepares the food (data processing). The waiter brings the food back to you (the response). You never enter the kitchen yourself.

### 2. REST

**What REST is:** REpresentational State Transfer.
**REST as an architectural style:** It is NOT a protocol (like HTTP) or a standard. It is a set of guidelines/constraints for how distributed systems should expose and manipulate resources over the web.
**Why REST is used:** It provides a standard, scalable, and predictable way to build APIs using existing HTTP features (methods, status codes, URLs). It is language-agnostic and highly cacheable.
**REST vs SOAP:** SOAP is a heavy, XML-based protocol with strict standards and envelopes. REST is a lightweight architectural style that usually uses JSON and relies on standard HTTP.
**REST vs GraphQL:** REST exposes multiple endpoints for different resources (e.g., `/users`, `/posts`). GraphQL exposes a single endpoint (`/graphql`) where the client specifies exactly what data shape it wants in a query language.
**REST vs RPC:** RPC (Remote Procedure Call) treats the API like function calls (e.g., `POST /createUser`, `POST /deleteUser`). REST treats the API like documents/resources (e.g., `POST /users`, `DELETE /users/1`).

#### REST Constraints

1. **Client-Server:**
   - _Meaning:_ The client (UI) and server (data storage) are completely separate.
   - _Why:_ Allows them to evolve independently.
   - _Example:_ A React frontend and a Spring Boot backend.
   - _Violation:_ Server-side rendering mixing UI logic with database access.
2. **Statelessness:**
   - _Meaning:_ The server stores NO session state about the client. Every request must contain all information needed to process it (e.g., a JWT token).
   - _Why:_ Massive horizontal scalability. Any server instance can handle any request.
   - _Example:_ Sending a JWT in the `Authorization` header on every single request.
   - _Violation:_ Storing a user's logged-in state in server memory (HTTP Sessions).
3. **Cacheability:**
   - _Meaning:_ Responses must explicitly state if they can be cached by the client or intermediaries.
   - _Why:_ Reduces network traffic and server load.
   - _Example:_ `Cache-Control: max-age=3600` header.
   - _Violation:_ Returning massive, unchanging lists without cache headers, forcing clients to re-download them constantly.
4. **Uniform Interface:**
   - _Meaning:_ The API must look consistent. Resources are identified in requests, and standard HTTP methods are used predictably.
   - _Why:_ Decouples the architecture, allowing clients to understand the API intuitively.
   - _Example:_ Using `GET /users` to fetch users and `DELETE /users/1` to delete one.
   - _Violation:_ Using `POST /users/1/delete` (RPC style).
5. **Layered System:**
   - _Meaning:_ The client cannot tell if it is connected directly to the end server or an intermediary (like a load balancer, proxy, or cache).
   - _Why:_ Enables load balancing, security firewalls, and shared caches.
   - _Example:_ AWS API Gateway sitting in front of your Spring Boot app.
   - _Violation:_ The backend requiring a direct, unbroken socket connection to the client that breaks if a proxy intercepts it.
6. **Code-on-Demand (Optional):**
   - _Meaning:_ Servers can temporarily extend client functionality by sending executable code.
   - _Why:_ To push features to the client.
   - _Example:_ A server sending a JavaScript widget to render a payment form.

### 3. REST Resources

**Resource-oriented design:** Everything in the system is a "resource" (a noun), not an action (a verb).
**Resource naming:** Always use plural nouns (`/users`, not `/user`). Use lowercase and hyphens (`/user-profiles`).
**Collections vs Individual Resources:**

- Collection: `GET /users` (returns a list)
- Individual: `GET /users/15` (returns one specific user)
  **Nested Resources & Relationships:** Used to show hierarchy, but avoid nesting too deep.
- `GET /users/15/orders` (Orders belonging to User 15).

**Good vs Bad Examples:**

- **Good:** `GET /users` | **Bad:** `GET /getUsers` (REST uses HTTP verbs; don't put verbs in the URL).
- **Good:** `POST /users` | **Bad:** `POST /createUser` (The POST method _means_ create).
- **Good:** `GET /users/15` | **Bad:** `GET /getUserById?id=15` (The ID identifies the resource, it should be in the path).

_Why is the good approach preferable?_ Predictability. If a developer knows the resource is `/users`, they instantly know how to create, read, update, and delete them just by changing the HTTP method, without reading documentation to find out if you called it `/create_user` or `/add_user`.

### 4. HTTP

- **Request:** What the client sends to the server.
- **Response:** What the server sends back.
- **Headers:** Metadata about the request/response (e.g., `Content-Type: application/json`, auth tokens).
- **Body:** The actual data payload (e.g., the JSON representing a new user).
- **URL:** The address of the resource.
- **Status line:** The first line of a response (e.g., `HTTP/1.1 200 OK`).
- **Request line:** The first line of a request (e.g., `GET /users HTTP/1.1`).

#### HTTP Methods

- **GET:**
  - _Purpose:_ Retrieve a resource.
  - _Use:_ Reading data. _When NOT to use:_ Modifying data or sending sensitive data (URLs are logged).
  - _Safe:_ Yes (doesn't change state). _Idempotent:_ Yes.
- **POST:**
  - _Purpose:_ Create a new resource or trigger an action.
  - _Use:_ Creating users, submitting forms. _When NOT to use:_ Simply retrieving data.
  - _Safe:_ No. _Idempotent:_ No (calling it twice creates two resources).
- **PUT:**
  - _Purpose:_ Full replacement of a resource.
  - _Use:_ Updating every field of a user.
  - _Safe:_ No. _Idempotent:_ Yes (replacing it with the same data 10 times yields the same final state).
- **PATCH:**
  - _Purpose:_ Partial update.
  - _Use:_ Updating just the user's email.
  - _Safe:_ No. _Idempotent:_ Often yes, but technically non-idempotent depending on the patch instructions (e.g., "increment age by 1" is not idempotent).
- **DELETE:**
  - _Purpose:_ Remove a resource.
  - _Safe:_ No. _Idempotent:_ Yes (deleting it once or 10 times leaves the resource deleted).

**GET vs POST:** GET requests data, puts params in the URL, has size limits, is cached, and is logged in server histories. POST submits data, puts it in the body, has no size limit, is not cached, and is secure for passwords.
**POST vs PUT:** POST creates a new child resource under a collection (e.g., `POST /users`). PUT replaces a specific resource (e.g., `PUT /users/15`). POST is non-idempotent; PUT is idempotent.
**PUT vs PATCH:** PUT requires sending the _entire_ object representation. If you omit a field, it should be set to null. PATCH only requires sending the specific fields you want to change.

### 5. HTTP Status Codes

- **1xx (Informational):** Request received, continuing process.
- **2xx (Success):** The action was successfully received, understood, and accepted.
- **3xx (Redirection):** Further action must be taken to complete the request.
- **4xx (Client Error):** The request contains bad syntax or cannot be fulfilled (the client messed up).
- **5xx (Server Error):** The server failed to fulfill an apparently valid request (the backend messed up).

#### Important Codes:

- **200 OK:** Standard success. Used for GET, PUT, PATCH.
- **201 Created:** Success, and a new resource was created. Used for POST.
- **204 No Content:** Success, but no data to return. Used for DELETE. (Misunderstanding: Don't use this if the client needs the deleted object data).
- **400 Bad Request:** Validation failed (e.g., missing email).
- **401 Unauthorized:** Authentication failed. The user is not logged in or token is expired.
- **403 Forbidden:** Authorization failed. The user IS logged in, but lacks permissions (e.g., a Student trying to delete an exam).
- **404 Not Found:** The resource doesn't exist.
- **405 Method Not Allowed:** Trying to POST to an endpoint that only accepts GET.
- **409 Conflict:** The request conflicts with current server state (e.g., registering an email that already exists).
- **422 Unprocessable Entity:** The JSON is syntactically valid, but semantically wrong (often used instead of 400 for deep validation errors).
- **500 Internal Server Error:** An unhandled exception crashed the backend.

**Deep Dives:**

- **401 vs 403:** 401 = "I don't know who you are." 403 = "I know exactly who you are, and you aren't allowed to do this."
- **404 vs 204:** If I search for `GET /users/99` and user 99 doesn't exist, return 404. If I successfully `DELETE /users/1`, return 204 (action succeeded, nothing to say).
- **400 vs 409:** 400 means the input itself is garbage (e.g., password too short). 409 means the input is perfectly fine, but the database state rejects it (e.g., email already taken).

### 6. API Request Components

`GET /api/v1/users/15/orders?status=paid&page=0&size=20`

1. **Path Parameters (`15`):** Identifies a specific resource in the hierarchy. Used for routing.
2. **Query Parameters (`?status=paid...`):** Provides extra instructions, filters, or sorting for the resource.
3. **Headers (Not visible in URL):** Metadata. e.g., `Authorization: Bearer <token>`.
4. **Request Body (Not visible in URL):** The JSON payload. Not used in GET requests.
5. **Cookies:** Data stored by the browser, sent automatically in headers. Good for web sessions, bad for stateless mobile APIs.

---

# PART 2 — QUERY PARAMETERS

### 1. What are Query Parameters?

Example: `GET /users?name=Ayman&age=20`

- **`?`**: Marks the end of the routing path and the start of the query string.
- **key**: The parameter name (`name`, `age`).
- **value**: The data assigned to the key (`Ayman`, `20`).
- **`&`**: Separates multiple key-value pairs.
- **URL Encoding:** URLs cannot contain spaces or special characters. A space becomes `%20`. `?name=Ayman%20Ali`.

### 2. Query Params vs Path Params

- **Path Param (`/users/15`):** Identifies the resource. Without `15`, the identity changes completely. It is mandatory for retrieving an individual item.
- **Query Param (`/users?id=15`):** Modifies a collection. It is optional.
- _REST Convention:_ Never use `?id=15` to fetch a single resource. Use path params to identify _what_ you want (`/users/15`), and query params to specify _how_ you want it filtered or formatted (`/users?role=admin`).

### 3. Common Query Parameter Use Cases

- **Filtering (`?status=active`):** Narrows down the collection based on exact matches.
- **Searching (`?search=ayman`):** Performs fuzzy logic or full-text search across multiple fields (name, email, etc).
- **Sorting (`?sort=-createdAt`):** Determines order. The `-` indicates descending order, while `sort=createdAt` is ascending.
- **Pagination (`?page=0&size=20`):** Limits the number of results returned to prevent crashing the server/database.
- **Multiple filters (`?status=active&role=teacher`):** AND condition. Must be both active and a teacher.
- **Date ranges (`?from=2026-01-01&to=2026-08-01`):** Bounding a search.
- **Multiple values (`?role=admin&role=teacher`):** Represents an OR condition / IN clause. (Return users who are admin OR teacher).

### 4. Pagination

**Why it exists:** Returning 1,000,000 rows in one response will cause OutOfMemory errors on the server, timeout the database, and freeze the client's browser.

- **Page/Size (Offset/Limit):** `?page=2&size=10`. The database skips the first 20 records and takes the next 10.
  - _Advantages:_ Easy to implement, allows jumping to a specific page (e.g., Page 5).
  - _Disadvantages:_ Slow on huge datasets (skipping 1,000,000 rows requires scanning them). Vulnerable to data shifting (if a row is inserted while the user clicks "Next Page", they might see a duplicate).
- **Cursor Pagination:** `?after=eyJpZCI6MTIzfQ==&limit=10`. Uses a unique, sequential pointer (like the ID or timestamp of the last seen row).
  - _Advantages:_ Extremely fast on large tables (uses database indexes directly: `WHERE id > 123`). Immune to data shifting.
  - _Disadvantages:_ Cannot jump to "Page 5". You can only go "Next" or "Previous". Perfect for infinite scroll (like Twitter/Instagram).

### 5. Filtering / Sorting / Searching Flow

1. Client sends `GET /users?role=admin&sort=name`.
2. Controller parses the query params (often into a DTO or individual variables).
3. Controller passes them to the Service layer.
4. Service layer validates business rules (e.g., "Is the user allowed to search for admins?").
5. Service passes them to the Repository layer.
6. Repository dynamically builds the SQL/NoSQL query (e.g., using JPA Specifications or QueryDSL). `SELECT * FROM users WHERE role = 'admin' ORDER BY name ASC`.
7. Results are paginated at the database level and wrapped in a `PagedResponse` DTO.

---

# PART 3 — SOFTWARE DESIGN PRINCIPLES

### What is a Design Principle?

- **Principle vs Pattern:** A principle is a high-level philosophy or rule of thumb (e.g., "Don't repeat yourself"). A pattern is a specific, structured solution to a recurring problem (e.g., "Use the Singleton pattern").
- **Principle vs Architecture:** Architecture is the macro-level structure of the system (Microservices, Monolith). Principles guide the micro-level code organization within that architecture.
- **Why they exist:** To manage complexity. Software rots over time. Principles prevent spaghetti code, make testing easier, and allow multiple developers to work together without breaking each other's code.
- **Harm of blind adherence:** Over-engineering. Applying strict principles to a simple throwaway script wastes time and makes the code harder to read. "Premature abstraction is the root of all evil."

## SOLID PRINCIPLES

### S — Single Responsibility Principle (SRP)

1. **Name:** Single Responsibility Principle.
2. **Definition:** "A class should have one, and only one, reason to change."
3. **Problem it solves:** Classes that do too many things (God Classes) become massive, impossible to test, and highly fragile.
4. **Why it exists:** If a class handles database logic, business rules, and email formatting, a change to the email template might accidentally break the database logic because they share variables.
5. **Bad example:**
   ```java
   public class EmployeeService {
       public void calculatePay() { /* business logic */ }
       public void saveToDatabase() { /* SQL logic */ }
       public void printReport() { /* HTML formatting */ }
   }
   ```
6. **Why it's bad:** It has three reasons to change: business rules change, database schema changes, or UI reporting changes.
7. **Refactored example:**
   ```java
   public class PayCalculator { public void calculatePay() {} }
   public class EmployeeRepository { public void save() {} }
   public class ReportGenerator { public void printReport() {} }
   ```
8. **Why it's better:** Changes are isolated. You can rewrite the ReportGenerator without touching the PayCalculator.
9. **Real-world analogy:** A chef cooks, a waiter serves, a cleaner cleans. If one person does all three, the restaurant fails during a rush.
10. **Common mistakes:** Simplifying it to "one method per class". A class can have 10 methods, as long as they all serve the _same single responsibility_ (e.g., a `StringValidator` class).
11. **How to recognize violations:** The class is named `UserManager` (the word "Manager" usually implies it does everything). The file is 2,000 lines long. You have to mock 15 different dependencies to write a unit test.

### O — Open/Closed Principle (OCP)

1. **Name:** Open/Closed Principle.
2. **Definition:** "Software entities should be open for extension but closed for modification."
3. **Problem it solves:** Modifying existing, tested, working code introduces bugs.
4. **Why it exists:** You should be able to add new features by writing _new_ code, not by altering _existing_ code.
5. **Bad example:**
   ```java
   public class PaymentProcessor {
       public void process(String type) {
           if (type.equals("CREDIT")) { /* process credit */ }
           else if (type.equals("PAYPAL")) { /* process paypal */ }
           // Adding Crypto requires modifying this tested class!
       }
   }
   ```
6. **Why it's bad:** Every time the business adds a payment method, you risk breaking the existing Credit and Paypal logic. The class grows indefinitely.
7. **Refactored example:**
   ```java
   public interface PaymentMethod { void process(); }
   public class CreditPayment implements PaymentMethod { public void process() {} }
   public class PaypalPayment implements PaymentMethod { public void process() {} }
   // To add Crypto, just create a new class. Zero changes to existing code.
   public class CryptoPayment implements PaymentMethod { public void process() {} }
   ```
8. **Why it's better:** Relies on **polymorphism**. The core system just calls `method.process()`. New behaviors are added by extending the interface, completely eliminating regression bugs in older logic.
9. **Real-world analogy:** A power outlet. It is closed for modification (you don't tear down the wall to add a new device). It is open for extension (you just plug a new appliance into the standard interface).
10. **Common mistakes:** Trying to make everything extensible from day one. Apply OCP only where requirements frequently change.

### L — Liskov Substitution Principle (LSP)

1. **Name:** Liskov Substitution Principle.
2. **Definition:** "Objects of a superclass should be replaceable with objects of a subclass without affecting the correctness of the program."
3. **Problem it solves:** Bad inheritance hierarchies that break application behavior.
4. **Why it exists:** To ensure that inheritance is used for "is-a" relationships based on _behavior_, not just properties.
5. **Bad example:**
   ```java
   public class Bird { public void fly() {} }
   public class Ostrich extends Bird {
       @Override
       public void fly() { throw new UnsupportedOperationException("I can't fly!"); }
   }
   ```
6. **Why it's bad:** If the system has a `List<Bird>` and calls `.fly()` on all of them, the Ostrich will crash the program. The subclass broke the behavioral contract of the parent.
7. **Refactored example:**
   ```java
   public interface Bird {}
   public interface FlyingBird extends Bird { void fly(); }
   public class Eagle implements FlyingBird { public void fly() {} }
   public class Ostrich implements Bird { /* no fly method */ }
   ```
8. **Why it's better:** The hierarchy accurately reflects behavior. No unexpected exceptions.
9. **Real-world analogy:** If I ask for a coffee maker (interface), and you give me an espresso machine (subclass), I can still make coffee. If you give me a tea kettle that explodes when I put coffee beans in it, you violated LSP.

### I — Interface Segregation Principle (ISP)

1. **Name:** Interface Segregation Principle.
2. **Definition:** "Clients should not be forced to depend on methods they do not use."
3. **Problem it solves:** "Fat" or "Polluted" interfaces that force implementing classes to write dummy methods.
4. **Why it exists:** To keep systems decoupled and specific.
5. **Bad example:**
   ```java
   public interface Worker {
       void work();
       void eat();
   }
   public class Robot implements Worker {
       public void work() { /* works */ }
       public void eat() { throw new RuntimeException("Robots don't eat!"); }
   }
   ```
6. **Why it's bad:** The Robot is forced to implement `eat()`, violating both ISP and LSP.
7. **Refactored example:**
   ```java
   public interface Workable { void work(); }
   public interface Eatable { void eat(); }
   public class Human implements Workable, Eatable { /* implements both */ }
   public class Robot implements Workable { /* only implements work */ }
   ```
8. **Why it's better:** Role-based interfaces. Classes only implement exactly what they need.
9. **Real-world analogy:** Buying a massive multi-tool when you only need a screwdriver. It's bulky and gets in the way.

### D — Dependency Inversion Principle (DIP)

1. **Name:** Dependency Inversion Principle.
2. **Definition:** "High-level modules should not depend on low-level modules. Both should depend on abstractions."
3. **Problem it solves:** Tight coupling. If a business logic class creates its own database connection, it is permanently chained to that specific database.
4. **Why it exists:** To make systems modular, testable, and interchangeable.
5. **Bad example:**
   ```java
   public class OrderService {
       private MySQLDatabase db = new MySQLDatabase(); // Tight coupling!
       public void save() { db.insert(); }
   }
   ```
6. **Why it's bad:** `OrderService` (high-level) depends directly on `MySQLDatabase` (low-level). You cannot unit test `OrderService` without spinning up a real MySQL database. You cannot easily switch to PostgreSQL.
7. **Refactored example:**

   ```java
   public interface Database { void insert(); } // Abstraction

   public class OrderService {
       private Database db;
       // Dependency is injected, but the PRINCIPLE is that it depends on the INTERFACE.
       public OrderService(Database db) { this.db = db; }
   }
   ```

8. **Why it's better:** `OrderService` only knows about the `Database` interface. It doesn't care if it's MySQL, Postgres, or a Mock database for testing.
9. **Real-world analogy:** A lamp (high-level) doesn't wire itself directly into the power plant (low-level). It plugs into a standard wall socket (abstraction). You can swap the power plant for solar panels, and the lamp still works.
10. **Critical Distinction:** **Dependency Injection (DI)** is the _technique_ of passing the dependency through the constructor. **Dependency Inversion (DIP)** is the _architectural principle_ that the dependency being passed must be an abstraction (interface), not a concrete class.

# PART 4 — OTHER IMPORTANT DESIGN PRINCIPLES

### DRY (Don't Repeat Yourself)

- **What it means:** Every piece of knowledge must have a single, unambiguous, authoritative representation within a system.
- **Duplication Types:** Code duplication (copy-pasting lines) vs Knowledge duplication (two different classes containing the same exact business rule).
- **When duplication is acceptable:** When two pieces of code look identical today, but will evolve for completely different business reasons tomorrow (e.g., separating an API DTO from a Database Entity, even if they have the exact same fields).
- **Premature abstraction:** Extracting code into a common helper class just because it looks similar, forcing two unrelated features to share a dependency. "Duplication is far cheaper than the wrong abstraction."

### KISS (Keep It Simple)

- **What it means:** Systems work best when they are kept simple rather than made complex.
- **Complexity vs Simplicity:** Avoid writing clever, unreadable one-liners. Avoid using a complex message broker (like Kafka) when a simple database table works for your current scale.
- **Overengineering:** Solving problems you don't have yet.

### YAGNI (You Aren't Gonna Need It)

- **What it means:** Always implement things when you actually need them, never when you just foresee that you need them.
- **Why developers over-engineer:** Fear of changing code later. But agile practices and good test coverage make changing code safe. Adding speculative features adds dead weight and maintenance costs.

### Separation of Concerns (SoC)

- **What it means:** Dividing a computer program into distinct sections so that each section addresses a separate concern.
- **Examples in backend:**
  - **Controllers:** Concern = HTTP routing and JSON serialization.
  - **Services:** Concern = Business rules and transactions.
  - **Repositories:** Concern = Database access and SQL.

### Encapsulation

- **What it means:** Bundling data and the methods that operate on that data into a single unit, and restricting direct access to some of the object's components.
- **Public vs Private:** State (variables) should be private. Behavior (methods) should be public.
- **Getters/Setters fallacy:** Having private variables with public getters/setters for _every single field_ does not mean you have good encapsulation. You are still exposing the entire internal state. Good encapsulation means hiding the state and exposing a behavior (e.g., `account.withdraw(50)` instead of `account.setBalance(account.getBalance() - 50)`).

### Abstraction

- **What it means:** Hiding the complex reality while exposing only the essential parts.
- **Interfaces / Abstract Classes:** Used to define _what_ something does, without exposing _how_ it does it.

### Composition Over Inheritance

- **What it means:** Classes should achieve polymorphic behavior and code reuse by containing instances of other classes that implement the desired functionality (Composition), rather than inheriting from a base class (Inheritance).
- **Why it's preferred:** Inheritance creates the tightest form of coupling in software. If the parent class changes, all children break. Java only allows single inheritance.
- **When inheritance is appropriate:** When there is a pure, undisputed "is-a" relationship (e.g., `CheckingAccount` is a `BankAccount`).

### Coupling

- **Tight vs Loose:** Tight coupling means classes are highly dependent on each other. A change in one forces a change in the other. Loose coupling means classes can operate independently, communicating via stable interfaces.
- **Impact:** High coupling makes code impossible to unit test and scary to refactor.

### Cohesion

- **High vs Low:** Cohesion refers to how closely related and focused the responsibilities of a single module/class are. High cohesion means everything in the class logically belongs together. Low cohesion means a random assortment of unrelated functions (e.g., `Utility.java`).
- **Relationship with SRP:** High cohesion naturally leads to the Single Responsibility Principle.

### Law of Demeter (Principle of Least Knowledge)

- **What it means:** A module should not know about the inner workings of the objects it manipulates. "Don't talk to strangers."
- **Example violation:** `customer.getWallet().getCreditCard().charge(50);` (The caller knows way too much about the customer's internals).
- **Better:** `customer.charge(50);` (Let the customer handle its own wallet).

### Principle of Least Surprise

- **What it means:** The behavior of a system or function should not surprise the developer using it.
- **Naming:** A method named `getUser()` should not also write to the database or send an email.

---

# PART 5 — DESIGN PATTERNS

**Important Distinction:** Design Principles (like SOLID) are abstract philosophies. Design Patterns (like Factory) are concrete, standardized templates for solving specific coding problems. You use Patterns to help achieve Principles.

### 1. Creational Patterns

Deal with object creation mechanisms.

- **Singleton:**
  - _Problem:_ You need exactly ONE instance of a class across the whole application (e.g., a Database Connection Pool).
  - _Intent:_ Ensure a class has only one instance and provide a global point of access.
  - _Disadvantage:_ Acts like a global variable. Hard to mock in tests. Spring Boot handles Singletons automatically via `@Bean`.
- **Factory Method:**
  - _Problem:_ You don't know the exact class of object you need to create until runtime.
  - _Intent:_ Define an interface for creating an object, but let subclasses decide which class to instantiate.
- **Abstract Factory:** Groups object factories that have a common theme.
- **Builder:**
  - _Problem:_ A class has a massive constructor with 15 optional parameters.
  - _Intent:_ Separate the construction of a complex object from its representation. (e.g., `User.builder().name("x").age(20).build();`).
- **Prototype:** Clones existing objects instead of creating new ones from scratch.

### 2. Structural Patterns

Deal with object composition and relationships.

- **Adapter:**
  - _Problem:_ Two classes can't work together because they have incompatible interfaces.
  - _Intent:_ Wrap an existing class with a new interface. (e.g., wrapping a legacy XML payment system to look like a modern JSON interface).
- **Decorator:**
  - _Problem:_ You want to add features to an object dynamically without using massive subclassing.
  - _Intent:_ Attach additional responsibilities to an object dynamically. (e.g., `new Sugar(new Milk(new Coffee()))`). Supports Composition over Inheritance.
- **Facade:**
  - _Problem:_ A complex subsystem has dozens of classes and is hard to use.
  - _Intent:_ Provide a single, simplified interface to the complex subsystem.
- **Proxy:** Provides a surrogate or placeholder for another object to control access to it (e.g., Hibernate lazy-loading proxies, or Spring Security proxies).
- **Composite:** Treats individual objects and compositions of objects uniformly (e.g., UI trees).

### 3. Behavioral Patterns

Deal with communication between objects.

- **Strategy:**
  - _Problem:_ You have a massive `if/else` or `switch` statement picking an algorithm.
  - _Intent:_ Define a family of algorithms, encapsulate each one, and make them interchangeable. (e.g., PaymentStrategy interface with CreditCard, PayPal implementations). Supports OCP.
- **Observer:**
  - _Problem:_ Multiple objects need to know when the state of another object changes.
  - _Intent:_ Define a one-to-many dependency so that when one object changes state, all dependents are notified automatically. (e.g., Event Listeners, Pub/Sub).
- **Command:** Encapsulates a request as an object, allowing parameterization of clients with queues, requests, and logging.
- **Template Method:** Defines the skeleton of an algorithm in a base class but lets subclasses override specific steps.
- **Chain of Responsibility:** Passes a request along a chain of handlers. Upon receiving a request, each handler decides either to process it or to pass it to the next handler in the chain. (e.g., Spring Security Filter Chain).

---

# PART 6 — PRINCIPLE VS PATTERN

| Concept                   | Definition                                            | Example                          |
| :------------------------ | :---------------------------------------------------- | :------------------------------- |
| **Design Principle**      | Abstract rule/philosophy for writing good code.       | Single Responsibility (SRP), DRY |
| **Design Pattern**        | Specific coding template to solve a common problem.   | Strategy, Builder, Singleton     |
| **Architectural Pattern** | Broad template for structuring an entire application. | MVC, Layered Architecture        |
| **Architecture**          | The actual physical/logical layout of the system.     | Microservices running on AWS EKS |

**Key Relationships:**

- **OCP → Strategy Pattern:** The Strategy pattern is the literal implementation of the Open/Closed Principle. It makes the context class "closed" for modification, while the new strategy implementations make it "open" for extension.
- **DIP → Dependency Injection:** Dependency Injection (a technique) is how we achieve the Dependency Inversion Principle (the philosophy).
- **SRP → Separation of Concerns (Architectural Pattern):** SRP applied at the macro level leads to layered architecture (Controller, Service, Repository).
- **Composition over Inheritance → Decorator / Strategy:** Both of these patterns favor holding a reference to an interface rather than extending a base class.

## _Crucial Mistake:_ Forcing a Design Pattern where a simple `if` statement would suffice violates the KISS principle. Patterns add boilerplate; use them only when the problem they solve is actively causing pain.

# PART 7 — CODE SMELL AND BAD DESIGN

**Code Smells** are surface indications that usually correspond to a deeper problem in the system.

- **Giant classes / God objects:** Classes with 2000+ lines. They violate SRP and Cohesion. Usually named `AppManager`, `Utils`, `SystemController`. _Fix:_ Split into smaller classes based on specific responsibilities.
- **Giant methods:** Methods with 200+ lines. Hard to test, hard to read. _Fix:_ Extract Method refactoring.
- **Too many if/else / switch statements:** Violates OCP. _Fix:_ Polymorphism and Strategy pattern.
- **Tight coupling:** Classes instantiating their own dependencies. _Fix:_ Dependency Injection (DIP).
- **Duplicate logic:** Copy-pasted business rules. Violates DRY. _Fix:_ Extract to a common shared service.
- **Feature envy:** A method accesses the data of another object more than its own data. _Fix:_ Move the method to the class whose data it is obsessing over.
- **Shotgun surgery:** You want to add one feature, but you have to make 20 small edits across 15 different files. Violates SRP and Cohesion.
- **Primitive obsession:** Using primitives (`String`, `int`) to represent domain concepts (e.g., using `String` for an Email, or `String` for a ZipCode). _Fix:_ Create a Value Object (`class Email { ... }`) that encapsulates the validation.
- **Long parameter lists:** `public void createUser(String n, String e, int a, String p, String role, boolean act)`. _Fix:_ Introduce a Parameter Object (DTO) or use the Builder pattern.

---

# PART 8 — PRACTICAL REFACTORING

### Example 1: SRP Violation

_Bad Code:_

```java
public class UserService {
    public void registerUser(String email, String password) {
        if (!email.contains("@")) throw new Error("Invalid email");
        db.save("INSERT INTO users...");
        smtp.send(email, "Welcome!");
    }
}
```

_Why it's bad:_ Validation, persistence, and notification are mixed.
_Refactored:_

```java
public class UserService {
    private final EmailValidator validator;
    private final UserRepository repository;
    private final EmailService emailer;

    public void registerUser(UserRequest req) {
        validator.validate(req.getEmail());
        repository.save(req);
        emailer.sendWelcome(req.getEmail());
    }
}
```

_Why it's better:_ Responsibilities are separated. We can test `UserService` by mocking the database and emailer.

### Example 2: OCP Violation

_Bad Code:_

```java
public class NotificationService {
    public void notify(String type, String msg) {
        if (type.equals("SMS")) { /* send sms */ }
        else if (type.equals("EMAIL")) { /* send email */ }
    }
}
```

_Refactored (Strategy Pattern):_

```java
public interface Notifier { void send(String msg); }
public class SmsNotifier implements Notifier { public void send(String msg) {} }
public class EmailNotifier implements Notifier { public void send(String msg) {} }

public class NotificationService {
    public void notify(Notifier notifier, String msg) {
        notifier.send(msg); // Open for extension, closed for modification!
    }
}
```

### Example 3: DIP Violation

_Bad Code:_

```java
public class ReportService {
    // Tight coupling to a specific implementation!
    private MySQLDatabase db = new MySQLDatabase();
}
```

_Refactored:_

```java
public class ReportService {
    private final Database db; // Depends on an Abstraction

    // Dependency is injected
    public ReportService(Database db) {
        this.db = db;
    }
}
```

### Example 4: ISP Violation

_Bad Code:_

```java
public interface Animal { void walk(); void fly(); }
public class Dog implements Animal {
    public void walk() {}
    public void fly() { throw new Error("Dogs don't fly"); }
}
```

_Refactored:_

```java
public interface Walkable { void walk(); }
public interface Flyable { void fly(); }
public class Dog implements Walkable { public void walk() {} }
```

### Example 5: LSP Violation

_Bad Code:_

```java
public class Rectangle {
    protected int w, h;
    public void setW(int w) { this.w = w; }
    public void setH(int h) { this.h = h; }
}
public class Square extends Rectangle {
    public void setW(int w) { this.w = w; this.h = w; }
}
```

_Why it's bad:_ If I have a `Rectangle r = new Square();`, and I call `r.setW(5); r.setH(10);`, the area is now 100 instead of 50. It breaks the mathematical expectation of a rectangle.
_Refactored:_ Don't use inheritance here. `Square` and `Rectangle` should implement a common `Shape` interface with a `getArea()` method.

### Example 6: DRY Violation vs Premature Abstraction

_Bad Code:_
You have a `Student` and a `Teacher` class. Both have a `String name`.
_Premature Abstraction:_ You create a `PersonBaseEntity` just to share the `name` field, forcing a database table join and complex inheritance just to save one line of code.
_Better:_ Leave the duplication. A Student and a Teacher evolve for completely different business reasons. Sharing a `name` field is incidental, not fundamental.

---

# PART 9 — HOW EVERYTHING CONNECTS

In a modern backend system (like Spring Boot), these concepts stack together seamlessly:

```mermaid
graph TD
    Client["Client (Browser/Mobile)"] -->|HTTP GET /users?role=admin (REST)| Controller["Controller (API Layer)"]
    Controller -->|Maps to DTO (SRP)| Service["Service (Business Logic)"]
    Service -->|Uses Interface (DIP)| Repository["Repository (Data Access)"]
    Repository -->|SQL| DB["Database"]
```

**Where the principles appear:**

- **REST & HTTP:** The Client and Controller communicate using standard methods (`GET`) and stateless URLs.
- **Query Parameters:** The `?role=admin` is passed from the Controller down to the Repository to filter the SQL query.
- **SRP (Separation of Concerns):** The Controller _only_ handles HTTP status codes. The Service _only_ handles business rules. The Repository _only_ handles database queries.
- **DIP:** The Service does not instantiate the Repository. It depends on a `UserRepository` interface, and Spring Boot injects the implementation.
- **DTOs:** We don't expose the Database Entity directly to the Client. We map it to a DTO, hiding internal state (Encapsulation).
- **OCP:** If we want to add a new way to calculate discounts, we don't rewrite the Service. We pass in a `DiscountStrategy` interface.

## _Remember:_ Principles **guide** the overall architecture. Patterns are the **tools** you reach for when you hit a specific roadblock while applying those principles.

# PART 10 — FINAL CHEAT SHEETS

### REST Cheat Sheet

| Concept         | Meaning                          | Example         |
| :-------------- | :------------------------------- | :-------------- |
| **Resource**    | A noun representing an entity.   | `/users`        |
| **Stateless**   | No session memory on the server. | JWT tokens      |
| **Path Param**  | Identifies a specific resource.  | `/users/5`      |
| **Query Param** | Filters/sorts a collection.      | `/users?age=20` |

### HTTP Methods

| Method     | Purpose            | Safe   | Idempotent |
| :--------- | :----------------- | :----- | :--------- |
| **GET**    | Read data          | ✅ Yes | ✅ Yes     |
| **POST**   | Create data        | ❌ No  | ❌ No      |
| **PUT**    | Replace completely | ❌ No  | ✅ Yes     |
| **PATCH**  | Update partially   | ❌ No  | ❌ No\*    |
| **DELETE** | Remove data        | ❌ No  | ✅ Yes     |

### Status Codes

| Code    | Meaning        | Usage                              |
| :------ | :------------- | :--------------------------------- |
| **200** | OK             | Success for GET/PUT/PATCH          |
| **201** | Created        | Success for POST                   |
| **204** | No Content     | Success for DELETE                 |
| **400** | Bad Request    | Client syntax/validation error     |
| **401** | Unauthorized   | Missing or invalid auth token      |
| **403** | Forbidden      | Valid token, but lacks permissions |
| **404** | Not Found      | Resource does not exist            |
| **409** | Conflict       | Business rule/database conflict    |
| **500** | Internal Error | The backend crashed                |

### SOLID Principles

| Principle | Meaning               | Typical Violation                               |
| :-------- | :-------------------- | :---------------------------------------------- |
| **S**     | Single Responsibility | A class doing DB, UI, and Business logic.       |
| **O**     | Open / Closed         | Adding a feature requires modifying a `switch`. |
| **L**     | Liskov Substitution   | Subclass throws `NotSupportedException`.        |
| **I**     | Interface Segregation | Forced to implement unused dummy methods.       |
| **D**     | Dependency Inversion  | Instantiating dependencies with `new`.          |

### General Principles

| Principle       | Core Idea                                                                   |
| :-------------- | :-------------------------------------------------------------------------- |
| **DRY**         | Don't Repeat Yourself (Avoid duplicated knowledge).                         |
| **KISS**        | Keep It Simple (Avoid overengineering).                                     |
| **YAGNI**       | You Aren't Gonna Need It (Don't build for hypothetical futures).            |
| **SoC**         | Separation of Concerns (Divide app by responsibility).                      |
| **Composition** | Prefer `has-a` relationships over `is-a` (inheritance) relationships.       |
| **Coupling**    | The degree to which classes depend on each other (Aim for Loose).           |
| **Cohesion**    | The degree to which elements inside a class belong together (Aim for High). |

### Design Patterns

| Pattern       | Category   | Problem Solved                                        |
| :------------ | :--------- | :---------------------------------------------------- |
| **Factory**   | Creational | Object creation is complex or dynamic.                |
| **Builder**   | Creational | Constructors have too many optional parameters.       |
| **Decorator** | Structural | Need to add behavior without massive subclassing.     |
| **Adapter**   | Structural | Two classes have incompatible interfaces.             |
| **Strategy**  | Behavioral | Swapping interchangeable algorithms at runtime (OCP). |
| **Observer**  | Behavioral | One object changes, multiple dependents need to know. |
