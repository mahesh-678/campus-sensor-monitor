import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    sensor_load: {
      executor: 'constant-arrival-rate',
      rate: 100,
      timeUnit: '1s',
      duration: '10s',
      preAllocatedVUs: 50,
      maxVUs: 250,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.05'],
  },
};

export default function () {
  const now = new Date().toISOString();
  const payload = [];

  for (let i = 0; i < 10; i++) {
    payload.push({
      sensorId: `load-sensor-${__VU}-${__ITER}-${i}`,
      type: 'temperature',
      value: 20 + Math.random() * 20,
      timestamp: now,
    });
  }

  const response = http.post(
    'http://localhost:8080/api/readings/batch',
    JSON.stringify(payload),
    {
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: '30s',
    }
  );

  check(response, {
    'status is 2xx': (r) => r.status >= 200 && r.status < 300,
  });
}