import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    burst: {
      executor: 'constant-arrival-rate',
      rate: 1000,
      timeUnit: '1s',
      duration: '1s',
      preAllocatedVUs: 100,
      maxVUs: 1000,
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
  const res = http.post('http://localhost:8080/api/readings/batch', JSON.stringify(payload), {
    headers: { 'Content-Type': 'application/json' },
    timeout: '30s',
  });
  check(res, { 'status is 2xx': (r) => r.status >= 200 && r.status < 300 });
}
