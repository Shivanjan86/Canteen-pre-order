# Canteen Pre-Order System

## 1. Project Overview

The Canteen Pre-Order System is a Spring Boot web application designed for canteen order booking with role-based access for Customer, Staff, and Admin users. The project supports registration, login, menu management, cart-based ordering, pickup slots, notifications, and operational reports.

Primary goals:
- Reduce queue time by allowing pre-order and pickup slots
- Provide operational visibility to canteen staff/admin
- Enforce role-based security and controlled access
- Demonstrate OOAD concepts and design-pattern usage

Technology stack:
- Backend: Java 17, Spring Boot 3.2.3
- REST API: Spring Web
- Persistence: Spring Data JPA + Hibernate
- Database: MySQL
- Security: Spring Security (HTTP Basic + role authorization)
- Frontend: Static HTML/CSS/JavaScript (role-specific routed pages)
- Build tool: Maven

## 2. Project Structure

### 2.1 Java Packages
- `com.pesu.canteen`
  - `config`: security configuration
  - `controller`: REST endpoints
  - `dto`: API and report data transfer objects
  - `model.entity`: JPA entities
  - `pattern`: OOAD design-pattern implementations
  - `repository`: Spring Data JPA repositories
  - `service.interfaces`: service contracts
  - `service.impl`: business logic

### 2.2 Frontend Files
- `src/main/resources/static/index.html`: login/register page
- `src/main/resources/static/customer/*`: customer UI pages
- `src/main/resources/static/staff/dashboard.html`: staff dashboard
- `src/main/resources/static/admin/dashboard.html`: admin dashboard
- `src/main/resources/static/js/*`: route/page scripts
- `src/main/resources/static/css/style.css`: styling

## 3. Core Functional Modules

### 3.1 Authentication and User Management
Capabilities:
- Register new users with role selection
- Login using email/password
- Role-aware post-login redirection
- Secure password storage using BCrypt

Flow:
1. User submits registration details (name, email, password, role)
2. Service checks duplicate email
3. User object is created through Factory pattern
4. Password is encrypted and user is persisted
5. On login, credentials are validated and role information is returned

Classes involved:
- `AuthController`
- `AuthenticationService` / `AuthenticationServiceImpl`
- `UserFactory`
- `CanteenUserDetailsService`
- `UserRepository`
- DTOs: `UserRegistrationDTO`, `AuthUserDTO`

### 3.2 Menu Management
Capabilities:
- Admin can add new menu items
- Customers can list available menu only

Flow:
1. Admin adds item via API
2. Service sets `available=true` by default
3. Menu list API returns only available items

Classes involved:
- `MenuController`
- `MenuService` / `MenuServiceImpl`
- `MenuRepository`
- `MenuItem` entity

### 3.3 Cart and Order Booking
Capabilities:
- Customer can choose menu items and add to cart in UI
- Customer can choose pickup slot
- Customer can place an order from cart
- Customer can view own order history
- Customer can cancel order (with business restrictions)
- Staff/Admin can update order status

Business rules currently implemented:
- Order cannot be cancelled if status is `PREPARING` or `READY`
- Pickup slot is stored for each order
- Status updates are normalized to uppercase

Classes involved:
- `OrderController`
- `OrderService` / `OrderServiceImpl`
- `OrderFacade`
- Command classes for cancel/status updates
- `OrderBuilder`
- `OrderRepository`
- `Order` entity

### 3.4 Notification Flow
Capabilities:
- Notification is generated when:
  - order is placed
  - order is cancelled
  - order status changes
- User can fetch own notifications
- User can mark notifications as read

Classes involved:
- `NotificationController`
- `NotificationService` / `NotificationServiceImpl`
- `NotificationRepository`
- `Notification` entity

### 3.5 Reports
Capabilities:
- Admin can fetch report summary containing:
  - total order count
  - count by status (PLACED/PREPARING/READY/CANCELLED)
  - revenue (excluding cancelled)
  - top 5 item frequencies

Classes involved:
- `ReportController`
- `ReportService` / `ReportServiceImpl`
- DTO: `ReportSummaryDTO`

## 4. API Endpoints

### 4.1 Auth
- `POST /api/auth/register`
- `POST /api/auth/login?email=...&password=...`

### 4.2 Menu
- `POST /api/menu` (Admin only)
- `GET /api/menu` (Authenticated)

### 4.3 Orders
- `POST /api/orders`
  - body: `customerId`, `menuItemIds`, `pickupSlot`
- `GET /api/orders/customer/{customerId}`
- `GET /api/orders/slots`
- `PUT /api/orders/{orderId}/cancel?userId=...`
- `PUT /api/orders/{orderId}/status?status=...`

### 4.4 Notifications
- `GET /api/notifications?userId=...`
- `PUT /api/notifications/{notificationId}/read?userId=...`

### 4.5 Reports
- `GET /api/reports/summary` (Admin only)

## 5. Security and Authorization

Security is configured using Spring Security HTTP Basic.

Public routes:
- `/`, `/index.html`, static assets
- `/api/auth/register`
- `/api/auth/login`

Protected API rules:
- `POST /api/menu` -> `ADMIN`
- `/api/reports/**` -> `ADMIN`
- `PUT /api/orders/*/status` -> `ADMIN` or `STAFF`
- `/api/orders/**` -> authenticated
- `/api/notifications/**` -> authenticated
- `GET /api/menu` -> authenticated

Authentication source:
- User credentials loaded from database via `CanteenUserDetailsService`
- Authorities derived from runtime type (Admin/Staff/default Customer)

Password handling:
- Stored with BCrypt hashes
- Login verifies with `PasswordEncoder.matches(...)`

## 6. Database Model

### 6.1 `users` (single-table inheritance)
- Base entity: `User`
- Subtypes: `Admin`, `Staff`
- Discriminator column: `role`

Fields:
- id
- name
- email
- password
- role (discriminator)

### 6.2 `menu_items`
Fields:
- id
- name
- price
- description
- available

### 6.3 `orders`
Fields:
- id
- customer_id (ManyToOne to users)
- status
- pickup_slot
- order_time

Relation table:
- `order_items` (ManyToMany between orders and menu_items)

### 6.4 `notifications`
Fields:
- id
- user_id (ManyToOne to users)
- message
- read_status
- created_at

## 7. UI Architecture and Navigation

### 7.1 Login and Role Routing
- Entry page: `index.html`
- After login:
  - CUSTOMER -> `/customer/menu.html`
  - STAFF -> `/staff/dashboard.html`
  - ADMIN -> `/admin/dashboard.html`

### 7.2 Customer Flow
1. Menu page (`customer/menu.html`)
   - loads slot list
   - loads available menu
   - add items to cart
2. Cart page (`customer/cart.html`)
   - review items and total
   - place order
3. Orders page (`customer/orders.html`)
   - view order history
   - view notifications and mark read

### 7.3 Staff Flow
- Dashboard (`staff/dashboard.html`)
  - update order status
  - fetch customer orders by ID

### 7.4 Admin Flow
- Dashboard (`admin/dashboard.html`)
  - add menu item
  - load reports summary

## 8. Design Pattern Usage (OOAD)

Implemented patterns used in codebase:

1. Factory Pattern
- `UserFactory`
- Used to create role-specific user objects (`Admin`, `Staff`, or default customer)

2. Builder Pattern
- `OrderBuilder`
- Used to construct `Order` object cleanly (customer, items, status, slot, time)

3. Facade Pattern
- `OrderFacade`
- Simplifies controller access to order subsystem

4. Command Pattern
- `OrderCommand` (interface)
- `CancelOrderCommand`
- `UpdateOrderStatusCommand`
- `OrderCommandInvoker`
- Encapsulates order state-change operations

## 9. Configuration and Environment

Maven dependencies:
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `mysql-connector-j`

Application properties:
- application name
- server port (8080)
- MySQL connection URL, user, password
- JPA options (`ddl-auto=update`, SQL logging)

Note:
- `MySQL8Dialect` property currently works but Hibernate warns it is deprecated in newer versions.

## 10. How to Run

Prerequisites:
- Java 17+
- Maven
- MySQL server running

Steps:
1. Ensure database credentials in `application.properties` are valid
2. Start app:
   - `mvn spring-boot:run`
3. Open browser:
   - `http://localhost:8080`

## 11. End-to-End Usage Scenario

1. Register one admin, one staff, one customer
2. Login as admin and add menu items
3. Login as customer, choose slot, add items to cart, place order
4. Login as staff and update order status (`PREPARING`/`READY`)
5. Login as customer and verify order status + notifications
6. Login as admin and view reports summary

## 12. Validation and Current Status

What is implemented and working:
- Role-based login and redirect
- Separate UI by role
- Menu management
- Cart-based ordering with slot selection
- Notifications generated from order lifecycle
- Reports summary
- Security authorization rules

Recent build status:
- Maven compile successful
- Server starts on port 8080

## 13. Known Limitations

- Frontend currently uses HTTP Basic credentials stored in localStorage for session convenience
- No JWT/session-token flow yet
- No advanced validation framework for DTO fields
- No dedicated automated tests under `src/test`
- Some APIs accept `userId` query parameter and rely on role security but do not yet cross-check principal user identity for strict ownership constraints

## 14. Recommended Next Improvements

1. Replace HTTP Basic frontend flow with JWT-based auth
2. Add Bean Validation annotations to DTOs
3. Introduce global exception handling (`@ControllerAdvice`)
4. Add integration tests for auth/order/report paths
5. Harden ownership checks by mapping authenticated principal to user ID in service layer
6. Add pagination/filtering for orders and notifications
7. Add richer analytics reports (time series, busiest slots)

## 15. Conclusion

The project now provides a full canteen pre-order lifecycle across three roles with layered backend architecture, applied OOAD design patterns, security controls, and route-based front-end UX. It is a strong functional base for an academic OOAD project and can be extended into production-grade architecture with token-based auth, stricter validation, and automated tests.
