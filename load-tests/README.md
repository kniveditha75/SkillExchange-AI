# Baseline & Load Testing Suite — SkillExchange AI

Automated Baseline and Concurrency Load Testing suite designed for benchmarking the **SkillExchange AI Backend Service** under sustained virtual user traffic, complete with automated **Excel Analysis reporting**.

## 📁 Folder Structure
```
load-tests/
├── package.json                   # Node.js run scripts
├── run-load-test.js               # Master load test runner & live telemetry
├── config/
│   └── load-test.config.js        # Benchmark settings (100 VUs, 60s, endpoints, SLAs)
├── engine/
│   └── load-engine.js             # High-performance async concurrent worker pool
├── reporters/
│   └── excel-reporter.js          # Load Test Excel Analysis Report Generator (.xlsx)
├── reports/
│   └── baseline-load-test-analysis.xlsx # Generated Excel Analysis Report
└── README.md
```

## ⚙️ Benchmark Specifications
- **Virtual Users (VUs):** `100 concurrent users`
- **Duration:** `60 seconds (1 minute continuous run)`
- **Throughput:** `7,000+ requests per minute`
- **Target Service:** `http://localhost:8000` (FastAPI Backend)

### Key Metrics Tracked
- **Requests Per Second (RPS):** Measured continuously (Target: ~120 - 130 req/sec).
- **Response Times (Latency):**
  - **Fastest (Min):** 40 - 200 ms
  - **Average:** ~250 ms
  - **Slowest (Max):** ~1,200 - 1,500 ms (1.5s)
  - **P95 & P99 Percentiles**
- **Availability / Success Rate:** % of successful 2xx/3xx HTTP responses vs 4xx/5xx errors.

## 🚀 How to Run

### 1. Run Standard 1-Minute Baseline Test (100 Virtual Users)
```bash
npm test
# OR
node run-load-test.js
```

### 2. Run Custom Duration Test
```bash
node run-load-test.js --duration 10
```

### 3. Generated Excel Analysis Report
The Excel analysis report is automatically generated at:
`load-tests/reports/baseline-load-test-analysis.xlsx`
- **Sheet 1 — Load KPI Dashboard:** High-level executive cards, SLA compliance verdicts, total requests, RPS, Min/Avg/Max/P95 latencies, and availability percentage.
- **Sheet 2 — Time-Series Analysis (1-60s):** Full 60-second second-by-second timeline tracking RPS, latency spikes, and error counts.
- **Sheet 3 — Endpoint SLA Breakdown:** Per-route performance analysis across `/`, `/projects`, `/auth/login`, `/notifications`, `/funding`, and `/users/profile`.

