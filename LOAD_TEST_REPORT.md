\# Campus Sensor Monitoring — Load Test Report



\## Summary



Functional testing and local performance testing have been completed

for the Campus Sensor Monitoring and Alerts application.



Environment: Windows local development machine.



\## Test 1: Python Sensor Simulation



Command:



```bash

python simulator/sensor\_simulator.py --count 10000 --batch-size 250 --workers 8

```



Results:



| Metric | Result |

|---|---:|

| Readings attempted | 10,000 |

| Readings accepted by API | 10,000 |

| Failed requests reported | 0 |

| Execution time | 1.981 seconds |

| Effective accepted throughput | 5,047.7 readings/second |



\## Test 2: k6 Sustained Load Test



Command:



```bash

k6 run loadtest/sensor-load.js

```



Configuration:

\- Target rate: 100 batch requests per second

\- Duration: 10 seconds

\- Readings per batch: 10

\- Scheduled batch requests: 1,000



Results:



| Metric | Result |

|---|---:|

| Completed HTTP requests | 1,000 |

| Successful HTTP checks | 1,000 / 1,000 |

| HTTP request failure rate | 0% |

| Average response time | 12.83 ms |

| Median response time | 11.55 ms |

| p90 response time | 14.88 ms |

| p95 response time | 16.90 ms |

| Maximum response time | 91.79 ms |



The completed requests contained 10,000 sensor readings in total.



\## Test 3: Initial Burst Test



The initial k6 test targeted 1,000 batch requests per second for one second.



| Metric | Result |

|---|---:|

| Completed HTTP requests | 736 |

| Dropped iterations | 265 |

| HTTP request failure rate | 0% |

| p95 response time | 521.12 ms |



The target request rate was not fully achieved. This result should not be interpreted as proof that the application sustained 1,000 requests per second.



\## Limitations



\- These tests were conducted on a local development machine.

\- The Python simulator's accepted-reading count was reported by the simulator.

\- The k6 test verified successful HTTP responses; it did not independently verify the persistence of every reading.

\- CPU utilization, memory consumption, and database resource usage were not recorded.

\- The results do not establish production capacity or performance under sustained high load.



\## Conclusion



The application successfully accepted the simulated readings and completed

the 10-second k6 test with no HTTP request failures.



Further testing is needed to characterize sustained capacity, resource usage,

and performance under progressively higher workloads.

