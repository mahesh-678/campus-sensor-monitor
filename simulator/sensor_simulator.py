import argparse
import concurrent.futures
import random
import time
from datetime import datetime, timezone
import requests

def reading(i):
    metric = random.choice(["temperature", "occupancy", "air_quality"])
    values = {"temperature": random.uniform(20, 40), "occupancy": random.randint(0, 150), "air_quality": random.randint(20, 220)}
    return {
        "sensorId": f"campus-building-{i % 20}-sensor-{i % 100}",
        "type": metric,
        "value": round(values[metric], 2),
        "timestamp": datetime.now(timezone.utc).isoformat()
    }

def main():
    parser = argparse.ArgumentParser(description="Campus sensor load simulator")
    parser.add_argument("--url", default="http://localhost:8080/api/readings/batch")
    parser.add_argument("--count", type=int, default=10000)
    parser.add_argument("--batch-size", type=int, default=250)
    parser.add_argument("--workers", type=int, default=16)
    args = parser.parse_args()

    payloads = [reading(i) for i in range(args.count)]
    batches = [payloads[i:i + args.batch_size] for i in range(0, len(payloads), args.batch_size)]
    session = requests.Session()
    def send(batch):
        response = requests.post(args.url, json=batch, timeout=30)
        return response.status_code, len(batch), response.text[:200]

    start = time.perf_counter()
    successes = 0
    failures = 0
    with concurrent.futures.ThreadPoolExecutor(max_workers=args.workers) as pool:
        futures = [pool.submit(send, batch) for batch in batches]
        for future in concurrent.futures.as_completed(futures):
            try:
                code, count, body = future.result()
                if 200 <= code < 300:
                    successes += count
                else:
                    failures += count
                    print(f"HTTP {code}: {body}")
            except Exception as exc:
                failures += args.batch_size
                print(f"Request failed: {exc}")
    elapsed = time.perf_counter() - start
    print("\\n--- Sensor load test ---")
    print(f"Readings attempted: {args.count}")
    print(f"Readings accepted by API: {successes}")
    print(f"Readings in failed requests (approx): {failures}")
    print(f"Elapsed seconds: {elapsed:.3f}")
    print(f"Effective accepted throughput: {successes / elapsed:.1f} readings/sec")
    print("Note: this script reports observed throughput; it does not guarantee a 10,000-reading/second burst.")

if __name__ == "__main__":
    main()
