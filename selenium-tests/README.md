# Selenium Web E2E Testing Suite — SkillExchange AI

Automated End-to-End (E2E) testing suite for the SkillExchange AI Web Application using Selenium WebDriver, Mocha, and automated Excel Analysis reporting.

## 📁 Folder Structure
```
selenium-tests/
├── package.json                   # Node.js dependencies and run scripts
├── run-tests.js                   # Master test runner & report coordinator
├── tests/
│   └── login-tests.js             # 320 comprehensive E2E test cases
├── reporters/
│   └── excel-reporter.js          # Excel Analysis spreadsheet engine (.xlsx)
├── reports/
│   └── web-test-report.xlsx       # Generated Excel analysis report
└── README.md
```

## 📊 Coverage Overview (320 Test Cases)
- **Section 1:** Welcome / Landing Page (TC-001 to TC-019)
- **Section 2:** Login Page & Authentication (TC-020 to TC-054)
- **Section 3:** Signup Page & Password Rules (TC-055 to TC-089)
- **Section 4:** Role Selection Page (TC-090 to TC-109)
- **Section 5:** Freelancer Pages (Dashboard, Marketplace, Details, Skill Analysis) (TC-110 to TC-149)
- **Section 6:** Idea Owner Pages (Dashboard, Create Project, Manage, Find Team) (TC-150 to TC-189)
- **Section 7:** Investor Pages (Dashboard, Marketplace, Portfolio) (TC-190 to TC-219)
- **Section 8:** Shared Pages (Chat, Notifications, Profile) (TC-220 to TC-259)
- **Section 9:** Navigation & Sidebar (TC-260 to TC-289)
- **Section 10:** UI/UX & Accessibility (TC-290 to TC-320)

## 🚀 How to Run

### 1. Install Dependencies
```bash
npm install
```

### 2. Run Tests and Generate Excel Report
```bash
npm test
# OR
node run-tests.js
```

### 3. Generated Excel Report
The Excel analysis report will be saved to:
`selenium-tests/reports/web-test-report.xlsx`
- **Sheet 1 — Executive Summary:** Total tests, pass rate, duration, section-by-section breakdown.
- **Sheet 2 — Detailed Test Results:** Every single test case with test ID, module name, scenario description, PASS/FAIL badge, execution time, and error notes.

