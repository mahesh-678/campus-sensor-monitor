# Campus Sensor Monitoring and Alerts

A real-time campus sensor monitoring system built with Java, Spring Boot, and PostgreSQL. The application ingests sensor readings through REST APIs, evaluates configurable alert rules, and generates alerts when monitored values cross specified thresholds.

The project also includes a Python sensor simulator and a k6 load-testing script for evaluating API behavior under concurrent workloads.

## Features

- **REST API:** Endpoints for health checks, sensor ingestion, alert rules, and alerts.
- **Single Reading Ingestion:** Accept individual sensor readings through an HTTP request.
- **Batch Ingestion:** Submit multiple sensor readings in one request.
- **Configurable Alert Rules:** Define metric types, comparison operators, thresholds, and evaluation windows.
- **Automatic Alert Generation:** Create alerts when incoming readings satisfy enabled rules.
- **PostgreSQL Integration:** Persist sensor readings, alert rules, and generated alerts.
- **Python Sensor Simulator:** Generate and submit large volumes of simulated sensor data.
- **Load Testing:** Use k6 to measure request throughput, response times, and HTTP failures.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application development |
| Spring Boot | REST API and application framework |
| Spring Data JPA | Database access and persistence |
| Hibernate | Object-relational mapping |
| PostgreSQL | Relational database |
| Maven | Dependency management and build |
| Python | Sensor simulation |
| Requests | HTTP communication from the simulator |
| k6 | API load testing |

## Architecture

The application follows this processing flow:

```text
Sensor Simulator / External Client
               |
               v
        Spring Boot REST API
               |
               v
       Reading Ingestion Service
               |
               v
       PostgreSQL Database
               |
               v
       Alert Rule Evaluation
               |
               v
        Generated Alerts
               |
               v
         Alerts REST API
```

Sensor readings are submitted individually or in batches. The application evaluates the readings against enabled alert rules and creates alerts when the configured conditions are satisfied.

## Project Structure

```text
campus-sensor-monitor/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/gdg/campus/
│       │       ├── controller/
│       │       ├── dto/
│       │       ├── model/
│       │       ├── repo/
│       │       └── service/
│       └── resources/
│           └── application.properties
├── simulator/
│   ├── sensor_simulator.py
│   └── requirements.txt
├── loadtest/
│   └── sensor-load.js
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Prerequisites

Install the following software before running the project:

- Java Development Kit
- Apache Maven
- PostgreSQL
- Python 3
- k6 (required for load testing)

Verify your installations:

```bash
java -version
mvn -version
python --version
psql --version
k6 version
```

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/mahesh-678/campus-sensor-monitor.git
cd campus-sensor-monitor
```

### 2. Configure PostgreSQL

Make sure PostgreSQL is installed and its service is running.

The default local database configuration is:

| Setting | Default value |
|---|---|
| Database | `campus_sensors` |
| Username | `campus` |
| Password | `campus123` |
| Port | `5432` |

If the database and user do not already exist, connect to PostgreSQL using an administrative account:

```bash
psql -U postgres -h localhost -p 5432 -d postgres
```

Create the application user:

```sql
CREATE USER campus WITH PASSWORD 'campus123';
```

Create the database:

```sql
CREATE DATABASE campus_sensors OWNER campus;
```

If the user or database already exists, do not create it again.

**Security note:** The default credentials are for local development only. Use appropriate credentials and environment variables for other environments.

### 3. Configure the database connection

The application supports these environment variables:

| Variable | Default |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/campus_sensors` |
| `DB_USERNAME` | `campus` |
| `DB_PASSWORD` | `campus123` |

Set different values if your PostgreSQL configuration differs from the defaults.

For PowerShell, you can set them for the current terminal session:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/campus_sensors"
$env:DB_USERNAME = "campus"
$env:DB_PASSWORD = "campus123"
```

### 4. Build the application

Run:

```bash
mvn clean package
```

### 5. Start the backend

Run:

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

Keep the application terminal open while testing the APIs.

### 6. Verify the health endpoint

In another terminal, run:

```bash
curl.exe -i http://localhost:8080/api/health
```

A successful health check should return an HTTP `200` response.

## API Reference

The following endpoints are implemented in the application.

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Check application health |
| POST | `/api/readings` | Submit one sensor reading |
| POST | `/api/readings/batch` | Submit a batch of readings |
| POST | `/api/rules` | Create an alert rule |
| GET | `/api/rules` | Retrieve alert rules |
| PUT | `/api/rules/{id}` | Update an alert rule |
| PATCH | `/api/rules/{id}/enabled?value=true` | Enable or disable a rule |
| DELETE | `/api/rules/{id}` | Delete an alert rule |
| GET | `/api/alerts` | Retrieve generated alerts |

### Example 1: Create an alert rule

This rule generates an alert when a temperature reading exceeds 40.

```bash
curl.exe -X POST http://localhost:8080/api/rules ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"High Temperature Alert\",\"type\":\"temperature\",\"operator\":\">\",\"threshold\":40,\"windowMinutes\":0}"
```

The request defines:

- Rule name: `High Temperature Alert`
- Metric type: `temperature`
- Operator: `>`
- Threshold: `40`
- Evaluation window: `0` minutes

### Example 2: Submit a sensor reading

```bash
curl.exe -X POST http://localhost:8080/api/readings ^
  -H "Content-Type: application/json" ^
  -d "{\"sensorId\":\"TEMP_SENSOR_001\",\"type\":\"temperature\",\"value\":45,\"timestamp\":\"2026-10-18T12:00:00Z\"}"
```

The timestamp must be an ISO 8601 timestamp.

The API accepts readings with a sensor ID, metric type, non-negative value, and timestamp.

### Example 3: Submit readings in a batch

The batch endpoint accepts a JSON array of readings.

```json
[
  {
    "sensorId": "TEMP_SENSOR_002",
    "type": "temperature",
    "value": 25,
    "timestamp": "2026-10-18T12:00:00Z"
  },
  {
    "sensorId": "TEMP_SENSOR_003",
    "type": "temperature",
    "value": 35,
    "timestamp": "2026-10-18T12:00:00Z"
  },
  {
    "sensorId": "TEMP_SENSOR_004",
    "type": "temperature",
    "value": 50,
    "timestamp": "2026-10-18T12:00:00Z"
  }
]
```

Submit the array to:

```text
POST http://localhost:8080/api/readings/batch
```

With an enabled temperature rule using a threshold of 40 and operator `>`, a reading of 50 satisfies the alert condition.

### Example 4: Retrieve alerts

```bash
curl.exe http://localhost:8080/api/alerts
```

### Example 5: Retrieve alert rules

```bash
curl.exe http://localhost:8080/api/rules
```

## Sensor Simulation

The Python simulator generates sensor data and submits it to the batch ingestion endpoint.

### Install dependencies

```bash
python -m pip install -r simulator/requirements.txt
```

### Run the simulator

Make sure the Spring Boot application is running first.

Run:

```bash
python simulator/sensor_simulator.py --count 10000 --batch-size 250 --workers 8
```

Parameters:

| Parameter | Description |
|---|---|
| `--count` | Number of sensor readings to generate |
| `--batch-size` | Number of readings per batch |
| `--workers` | Number of concurrent workers |
| `--url` | Target batch ingestion endpoint |

The default endpoint is:

```text
http://localhost:8080/api/readings/batch
```

## Load Testing with k6

The project includes a k6 script for evaluating the API under a scheduled request rate.

### Run the test

Ensure the Spring Boot application and PostgreSQL are running.

Execute:

```bash
k6 run loadtest/sensor-load.js
```

The tested configuration schedules 100 batch requests per second for 10 seconds, with 10 sensor readings in each batch.

The test reports metrics including:

- Total HTTP requests
- HTTP request failure rate
- Average response time
- Median response time
- 90th-percentile response time
- 95th-percentile response time
- Maximum response time
- Completed iterations

The script also checks whether API responses have successful HTTP status codes.

## Performance Testing Results

The following measurements were obtained during local testing.

### Python Sensor Simulation

| Metric | Result |
|---|---:|
| Readings attempted | 10,000 |
| Readings accepted by API | 10,000 |
| Failed requests reported | 0 |
| Elapsed time | 1.981 seconds |
| Effective accepted throughput | 5,047.7 readings/second |

Test command:

```bash
python simulator/sensor_simulator.py --count 10000 --batch-size 250 --workers 8
```

### k6 Sustained Load Test

| Metric | Result |
|---|---:|
| Test duration | 10 seconds |
| Target batch request rate | 100 requests/second |
| Completed HTTP requests | 1,000 |
| Readings per batch | 10 |
| Readings submitted in request payloads | 10,000 |
| HTTP requests failed | 0 |
| HTTP request failure rate | 0% |
| Average response time | 12.83 ms |
| Median response time | 11.55 ms |
| 90th-percentile latency | 14.88 ms |
| 95th-percentile latency | 16.90 ms |
| Maximum response time | 91.79 ms |

Test command:

```bash
k6 run loadtest/sensor-load.js
```

All 1,000 completed HTTP requests returned successful responses during this test.

### Interpreting the Results

The Python simulator reported that all 10,000 generated readings were accepted. The k6 test completed 1,000 HTTP requests, each containing 10 sensor readings.

These results describe separate local test runs. They demonstrate successful request processing under the tested conditions but do not independently verify the persistence of every submitted reading.

The k6 test measured approximately 100 batch requests per second, equivalent to approximately 1,000 readings per second offered in the request payloads.

**These measurements are from a local development environment and are not production capacity guarantees.** Performance may vary with hardware, database configuration, workload, network conditions, and concurrency.

## Future Improvements

Potential improvements include:

- Add a dashboard for visualizing sensor readings and active alerts.
- Introduce authentication and authorization for API endpoints.
- Add unit and integration tests.
- Evaluate connection pooling and database write performance.
- Test sustained workloads at progressively higher request rates.
- Add monitoring, structured logging, and deployment configuration.
- Improve alert lifecycle management and notification delivery.

## Author

**Mahesh Raj J R**

GitHub: [mahesh-678](https://github.com/mahesh-678)

