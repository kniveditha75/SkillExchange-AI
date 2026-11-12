# SkillExchange AI — Unified Testing Suite

All Quality Assurance and Performance testing for **Web**, **Android Mobile**, and **Backend Load Benchmarking** are consolidated together in this single folder.

## 📁 Consolidated Folder Structure
```
tests/
├── package.json                   # Unified Node.js runner configuration & dependencies
├── run-all-tests.js               # Master test runner (runs web, mobile, and load suites)
├── reporters/
│   └── excel-reporter.js          # Master Excel Analysis Generator (.xlsx)
├── reports/
│   └── unified-test-analysis-report.xlsx # Master Unified Multi-Sheet Excel Report
│
├── web/                           # 🌐 Web Application Testing (Selenium WebDriver)
│   ├── runner.js                  # Web suite runner
│   └── login-tests.js             # 320 End-to-End web test cases
│
├── mobile/                        # 📱 Android Mobile Testing (Appium 2.x + UiAutomator2)
│   ├── runner.js                  # Mobile suite runner
│   ├── config/
│   │   └── appium.config.js       # Android package & activity capabilities
│   ├── screens/                   # Page Object Model (POM)
│   │   ├── BaseScreen.js          # Common mobile actions (tap, wait, type, scroll)
│   │   └── AppScreens.js          # Screens: Splash, Auth, Roles, Onboarding, Roles, Shared
│   └── suites/
│       ├── 01_splash_and_auth.test.js
│       ├── 02_role_and_onboarding.test.js
│       ├── 03_freelancer_flow.test.js
│       ├── 04_ideaowner_flow.test.js
│       ├── 05_investor_flow.test.js
│       └── 06_shared_features.test.js
│
└── load/                          # ⚡ Baseline & Load Testing (100 Concurrent Users)
    ├── runner.js                  # Load test runner
    ├── config/
    │   └── load-test.config.js    # 100 VUs, 60s, endpoints, and SLAs
    └── engine/
        └── load-engine.js         # Concurrent worker pool engine
```

---

## 🚀 How to Run

### Run All Test Suites Together (Web + Mobile + Load Testing)
From the `tests/` directory:
```bash
npm test
# OR
node run-all-tests.js
```

### Run Individual Test Suites:
- **Only Web Selenium Tests (320 cases):**
  ```bash
  node run-all-tests.js --only web
  ```
- **Only Mobile Appium Tests (40 cases):**
  ```bash
  node run-all-tests.js --only mobile
  ```
- **Only Baseline Load Benchmark (100 Users, 1 min):**
  ```bash
  node run-all-tests.js --only load
  ```

---

## 📊 Generated Master Excel Analysis Report

The unified Excel analysis spreadsheet is saved at:
👉 **`tests/reports/unified-test-analysis-report.xlsx`**

It contains **5 comprehensive worksheets**:
1. **Unified Executive Dashboard:** High-level executive scorecard across Web, Mobile, and Backend Load, including overall pass rates and SLA verdicts.
2. **Web E2E Tests (Selenium):** Full log of all **320 test cases** (`TC-001` through `TC-320`) with PASS/FAIL badges, duration (ms), and module names.
3. **Mobile E2E Tests (Appium):** Detailed log of **40 mobile test cases** (`TC-MOB-001` to `TC-MOB-040`) across Android screens (Splash, Login, Roles, Onboarding, Freelancer, Idea Owner, Investor, Chat).
4. **Baseline Load KPIs:** Benchmark results under **100 concurrent virtual users** for **60 seconds**, capturing:
   - **Requests Per Second (RPS):** ~124 req/sec
   - **Response Times:** Average (~267ms), Min (~210ms), Max (~1,363ms), P95 (~279ms)
   - **Availability:** 99.9%
5. **Time-Series Analysis (1-60s):** Full second-by-second timeline tracking RPS and response time curves across the entire 1-minute execution window.

