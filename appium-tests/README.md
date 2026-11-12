# Appium Android Mobile E2E Testing Suite — SkillExchange AI

Automated End-to-End mobile testing suite for the SkillExchange AI Android Application using **Appium 2.x**, **UiAutomator2**, and automated **Excel Analysis reporting**.

## 📁 Folder Structure
```
appium-tests/
├── package.json                   # Node.js dependencies and run scripts
├── run-appium-tests.js            # Master test runner & report coordinator
├── config/
│   └── appium.config.js           # Android capabilities (com.simats.skillexchange)
├── screens/                       # Page Object Model (POM) for Android Compose
│   ├── BaseScreen.js              # Core mobile actions (tap, wait, type, scroll)
│   └── AppScreens.js              # Screens (Splash, Auth, Roles, Freelancer, IdeaOwner, Investor, Shared)
├── tests/
│   ├── 01_splash_and_auth.test.js     # Splash, Welcome, Login, Signup
│   ├── 02_role_and_onboarding.test.js # Role selection, Onboarding
│   ├── 03_freelancer_flow.test.js     # Freelancer flow (Marketplace, Proposals, Skills)
│   ├── 04_ideaowner_flow.test.js      # Idea Owner flow (Projects, Teams, Investors)
│   ├── 05_investor_flow.test.js       # Investor flow (Startups, Deals, Portfolio)
│   └── 06_shared_features.test.js     # Chat, Notifications, Profile, Logout
├── reporters/
│   └── excel-reporter.js          # Excel Analysis spreadsheet engine (.xlsx)
├── reports/
│   └── mobile-test-report.xlsx    # Generated Excel analysis report
└── README.md
```

## 📱 Mobile App Details
- **Package Name:** `com.simats.skillexchange`
- **Main Activity:** `com.simats.skillexchange.MainActivity`
- **UI Framework:** Jetpack Compose (Kotlin)
- **Min SDK:** 24 | **Target SDK:** 35

## 🚀 How to Run

### 1. Prerequisites (For Live Device / Emulator Execution)
1. Install Appium 2.x and the UiAutomator2 driver:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
2. Start the Appium Server:
   ```bash
   appium
   ```
3. Connect an Android device with USB debugging enabled, or start an Android Emulator via Android Studio.

### 2. Install Project Dependencies
```bash
npm install
```

### 3. Run Mobile Tests & Generate Excel Analysis Report
```bash
npm test
# OR
node run-appium-tests.js
```

### 4. Inspect the Excel Report
The analysis report is automatically generated at:
`appium-tests/reports/mobile-test-report.xlsx`
- **Sheet 1 — Mobile Executive Summary:** Total tests, pass rate, suite duration, Android package details, screen-by-screen breakdown.
- **Sheet 2 — Mobile Test Cases:** Detailed log of each test case (`TC-MOB-001` to `TC-MOB-040`), screen name, action description, PASS/FAIL badge, and execution time (ms).

