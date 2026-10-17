# Campus Sensor Monitoring and Alerts

A one-day MVP for the GDG Tech Wildcard backend task. It uses Spring Boot, PostgreSQL, configurable alert rules, rolling-window average rules, alert lifecycle endpoints, and a Python sensor simulator.

## Architecture

`Sensor Simulator / Client -> Spring Boot REST API -> PostgreSQL`

- Single readings and batches are accepted by the API.
- Readings are persisted before rule evaluation completes.
- Instant rules compare each reading to a threshold.
- Window rules calculate the average for the same sensor and metric over the configured number of minutes.
- A rule/sensor pair has at most one active alert (`OPEN` or `ACKNOWLEDGED`) at a time.
- Alerts can be acknowledged and resolved.
- PostgreSQL indexes support common sensor/time and alert-status lookups.

**Trade-off:** This first version uses synchronous rule evaluation and database-backed aggregation. It prioritizes correctness and a small, testable architecture over maximum throughput. For a production-scale version, put ingestion behind Kafka or another durable queue, use independent workers, add idempotency keys and retry/replay handling, and maintain window aggregates in a stream processor. Do not claim 10,000 readings/second unless your load test proves it.

## Requirements

- Java 21
- Maven 3.9+
- Docker Desktop (or a local PostgreSQL 16 instance)
- Python 3.10+ for the simulator
- Optional: k6 for the load test

## Run on Windows PowerShell

1. Unzip the project and open PowerShell in the project directory.
2. Start PostgreSQL:

   ```powershell
   docker compose up -d
   ```

3. Start the API:

   ```powershell
   mvn spring-boot:run
   ```

4. Check health:

   ```powershell
   Invoke-RestMethod http://localhost:8080/api/health
   ```

If Maven is not installed, use IntelliJ IDEA to open `pom.xml`, allow Maven to import dependencies, then run `CampusSensorApplication`.

## Run on macOS/Linux

```bash
docker compose up -d
mvn spring-boot:run
```

## API examples

### Create an instant rule

```bash
curl -X POST http://localhost:8080/api/rules \
  -H "Content-Type: application/json" \
  -d '{"name":"Hot library","type":"temperature","operator":">","threshold":32,"windowMinutes":0}'
```

### Create a 10-minute average rule

```bash
curl -X POST http://localhost:8080/api/rules \
  -H "Content-Type: application/json" \
  -d '{"name":"Library average too hot","type":"temperature","operator":">","threshold":30,"windowMinutes":10}'
```

### Submit one reading

```bash
curl -X POST http://localhost:8080/api/readings \
  -H "Content-Type: application/json" \
  -d '{"sensorId":"library-floor-2","type":"temperature","value":34.5,"timestamp":"2026-10-17T10:30:00Z"}'
```

### Submit a batch

```bash
curl -X POST http://localhost:8080/api/readings/batch \
  -H "Content-Type: application/json" \
  -d '[
    {"sensorId":"library-floor-2","type":"temperature","value":34.5,"timestamp":"2026-10-17T10:30:00Z"},
    {"sensorId":"library-floor-2","type":"temperature","value":35.0,"timestamp":"2026-10-17T10:31:00Z"}
  ]'
```

### Manage rules

```bash
curl http://localhost:8080/api/rules
curl -X PATCH "http://localhost:8080/api/rules/1/enabled?value=false"
curl -X PATCH "http://localhost:8080/api/rules/1/enabled?value=true"
curl -X PUT http://localhost:8080/api/rules/1 -H "Content-Type: application/json" \
  -d '{"name":"Hot library updated","type":"temperature","operator":">=","threshold":33,"windowMinutes":0}'
```

### View and manage alerts

```bash
curl http://localhost:8080/api/alerts
curl "http://localhost:8080/api/alerts?status=OPEN"
curl -X PATCH http://localhost:8080/api/alerts/1/acknowledge
curl -X PATCH http://localhost:8080/api/alerts/1/resolve
```

## Run the simulator

```powershell
cd simulator
python -m pip install -r requirements.txt
python sensor_simulator.py --count 10000 --batch-size 250 --workers 16
```

The script submits 10,000 readings in concurrent batches and prints the observed effective throughput. It is a practical smoke/load test, not proof of 10,000 readings in one second.

## Optional k6 test

The supplied `loadtest/sensor-load.js` is an initial k6 workload. Install k6, then run from the project root:

```bash
k6 run loadtest/sensor-load.js
```

This script uses batched requests at a high arrival rate; interpret request rate and reading rate separately. Tune batch size, VUs, database resources, and duration as necessary. Save the terminal output in `LOAD_TEST_REPORT.md`, including machine specs and exact settings.

## Load-test report template

Copy this into `LOAD_TEST_REPORT.md` after running your test; fill it with actual results.

- Date/time:
- OS and CPU/RAM:
- Java version:
- PostgreSQL version and configuration:
- Test tool and exact command:
- Number of readings attempted:
- Number of readings accepted:
- Number of readings persisted:
- Total elapsed time:
- Effective readings/second:
- p50/p95/p99 request latency (if measured):
- Error rate:
- Bottleneck observed:
- Optimization made and before/after result:

## Important limitations to disclose

- This MVP does not yet use Kafka or another durable queue; ingestion and evaluation run synchronously.
- Window aggregation queries PostgreSQL for each relevant rule/reading and may become expensive under a large burst.
- Alert deduplication is implemented at application level, so simultaneous workers could race. For a production deployment, enforce uniqueness/locking at the database level.
- Window evaluation uses the reading's event timestamp. Late-arriving data can affect prior windows; event-time watermarks/recomputation are not implemented.
- There is no authentication/authorization layer; add it if the API is exposed beyond local development.
- The load-test results must be measured on the target machine. Do not claim the 10,000/second target without evidence.
