# Project Pulse

A full-stack web application for managing senior design sections, teams, Weekly Activity Reports (WARs), and peer evaluations.

## Tech Stack
- **Frontend**: Vue 3 + Vuetify 3
- **Backend**: Spring Boot 3.3.x (Java 17)
- **Database**: PostgreSQL

## How to Run Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+
- Docker (for PostgreSQL) or a local PostgreSQL install

### Step 1: Start the Database
```bash
docker-compose up -d
```
This starts PostgreSQL on port 5432 with database `projectpulse`.

### Step 2: Start the Backend
```bash
cd backend
mvn spring-boot:run
```
The API runs on **http://localhost:8080**

On first run, an admin account is auto-created:
- **Email**: admin@projectpulse.com
- **Password**: Admin123!

### Step 3: Start the Frontend
```bash
cd frontend
npm install
npm run dev
```
The app runs on **http://localhost:5173**

### Step 4: Open the App
Go to **http://localhost:5173** and log in with the admin credentials above.

## Default Admin Credentials
| Field    | Value                    |
|----------|--------------------------|
| Email    | admin@projectpulse.com   |
| Password | Admin123!                |

## Environment Variables (Backend)
| Variable      | Default                | Description              |
|---------------|------------------------|--------------------------|
| DB_URL        | jdbc:postgresql://...  | PostgreSQL connection URL |
| DB_USERNAME   | postgres               | DB username              |
| DB_PASSWORD   | postgres               | DB password              |
| JWT_SECRET    | (auto)                 | JWT signing secret       |
| MAIL_HOST     | smtp.gmail.com         | SMTP host                |
| MAIL_USERNAME | noreply@projectpulse.com | From email             |
| MAIL_PASSWORD | (empty)                | Email password           |
| BASE_URL      | http://localhost:5173  | Frontend URL for links   |

## User Roles
- **Admin**: Manages sections, teams, rubrics, students, and instructors
- **Student**: Submits WARs, submits peer evaluations, views own report
- **Instructor**: Views team/student reports, peer eval reports
