# UserAuth with Duty Scheduler 🔐📅

This Spring Boot application provides user authentication, role-based access control, and a duty scheduling system with email notifications. It supports admin and user roles, schedule management, and shift reminders.

## 🔧 Technologies Used

- Java 21
- Spring Boot
- Spring Security
- Thymeleaf
- Spring Mail
- PostgreSQL or H2
- Liquibase (db.changelog)

## ✨ Features

- 🔐 User registration and login with roles (`USER`, `ADMIN`)
- 🔑 Custom user authentication via `CustomUserDetailsService`
- 📅 Duty scheduling system with shift types
- 📧 Email reminders via `EmailService`
- ❌ Absence handling with automatic shift reassignment
- 📊 Shift statistics and manual reassignment
- 🔒 Secured endpoints with Spring Security
- 📁 Database versioning with Liquibase

## 📁 Project Structure

src/
└── main/
├── java/
│ └── firstdb/
│ ├── controllers/
│ ├── exceptions/
│ ├── model/
│ ├── repositories/
│ ├── services/
│ │ ├── impl/
│ │ ├── schedule/
│ │ ├── user/
│ │ └── security/
│ ├── CustomUserDetailsService.java
│ ├── UserAuthApplication.java
└── resources/
├── templates/
├── static.images/
├── db.changelog/
└── application.properties

## 🚀 How to Run

## bash
mvn clean spring-boot:run
Then go to: http://localhost:8080

Default roles: USER, ADMIN

Use admin credentials to manage users and schedules

## 📬 Email Notification Logic
At midnight, the system checks today's duty

Sends an email to the assigned user (e.g. “You are on kitchen duty today”)

If marked absent → finds available replacement (based on fewest past duties)

Sends email to replacement

If no one available → notifies the admin

## 🧩 Absence Handling Logic
Users can mark themselves as absent via UI

Replacement is chosen from available users with the lowest shift count

All notifications are sent via email in real time or scheduled task

## 🧩 Database Management
Database schema is managed using Liquibase

All changesets are stored under resources/db.changelog/

## 📄 License
This project is private and intended for educational/demonstration purposes.

## 👤 Author
Vladislav Bondarevs