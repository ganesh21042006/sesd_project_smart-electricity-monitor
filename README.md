# Smart Electricity Usage Monitor

A runnable SESD full-stack starter project:
- Java 17 + Spring Boot REST API
- MySQL + Spring Data JPA
- HTML, CSS, JavaScript + Chart.js frontend
- Meter reading history, monthly usage, simple month-end forecast,
  200-unit threshold monitor, illustrative bill estimate and tips.

## Requirements
- JDK 17+
- Maven 3.8+
- MySQL 8+

## Run
1. Create the database: run `database/schema.sql` in MySQL Workbench.
2. Edit `backend/src/main/resources/application.properties` and set your MySQL password.
3. Open a terminal in `backend` and run: `mvn spring-boot:run`
4. Serve the `frontend` folder using VS Code Live Server (or another local static server).
5. Open the served `index.html`. The backend API runs at `http://localhost:8080`.

## API
- `POST /api/electricity/reading` JSON: `{"readingDate":"2026-11-01","meterReading":1000}`
- `GET /api/electricity/dashboard`
- `GET /api/electricity/history`

## Important notes
- Enter cumulative meter readings (the total displayed on the meter), not daily units.
- The first entry is a baseline and therefore records 0 units consumed. Add a second reading to calculate consumption.
- This starter version stores one household's readings and does not yet include authentication or appliance-level metering.
- The monthly forecast is a straight-line estimate based on month-to-date readings. It is not a machine-learning prediction.
- The bill estimate uses a clearly illustrative ₹5/unit assumption only on forecast units above 200. It is NOT an official Telangana tariff or a guarantee of Gruha Jyothi eligibility. Replace it with verified current tariff and scheme rules before relying on bill amounts.
- For production, add authentication, household/user ownership, stricter validation, CORS restrictions, automated tests, and secure configuration.
