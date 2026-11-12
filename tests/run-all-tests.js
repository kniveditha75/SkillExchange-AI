const path = require('path');
const fs = require('fs');

const { runWebSuite } = require('./web/runner');
const { runMobileSuite } = require('./mobile/runner');
const { runLoadSuite } = require('./load/runner');
const { generateUnifiedExcelReport } = require('./reporters/excel-reporter');

const UNIFIED_REPORT_PATH = path.join(__dirname, 'reports', 'unified-test-analysis-report.xlsx');

const args = process.argv.slice(2);
const onlyFlagIdx = args.indexOf('--only');
const targetFilter = onlyFlagIdx !== -1 ? args[onlyFlagIdx + 1] : 'all';

async function main() {
  console.log('╔═══════════════════════════════════════════════════════════════════╗');
  console.log('║  SkillExchange AI — Comprehensive Unified QA & Performance Suite   ║');
  console.log('╚═══════════════════════════════════════════════════════════════════╝');
  console.log(`Execution Mode : ${targetFilter.toUpperCase()}`);
  console.log(`Target Report  : ${UNIFIED_REPORT_PATH}\n`);

  let webResult = null;
  let mobileResult = null;
  let loadResult = null;

  // 1. WEB TESTING (SELENIUM)
  if (targetFilter === 'all' || targetFilter === 'web') {
    console.log('---------------------------------------------------------------------');
    console.log(' [1/3] Executing Web E2E Test Suite (Selenium WebDriver)');
    console.log('---------------------------------------------------------------------');
    webResult = await runWebSuite((curr, total, pass, fail) => {
      const pct = Math.round((curr / total) * 100);
      console.log(`   Web Suite Progress: [${pct}%] ${curr}/${total} tests (Passed: ${pass}, Failed: ${fail})`);
    });
    console.log(` Web Suite Complete: ${webResult.passed}/${webResult.total} passed (${webResult.passRate})\n`);
  }

  // 2. ANDROID MOBILE TESTING (APPIUM)
  if (targetFilter === 'all' || targetFilter === 'mobile') {
    console.log('---------------------------------------------------------------------');
    console.log(' [2/3] Executing Android Mobile E2E Test Suite (Appium UiAutomator2)');
    console.log('---------------------------------------------------------------------');
    mobileResult = await runMobileSuite((curr, total, pass, fail) => {
      const pct = Math.round((curr / total) * 100);
      console.log(`   Mobile Suite Progress: [${pct}%] ${curr}/${total} tests (Passed: ${pass}, Failed: ${fail})`);
    });
    console.log(` Mobile Suite Complete: ${mobileResult.passed}/${mobileResult.total} passed (${mobileResult.passRate})\n`);
  }

  // 3. BASELINE / LOAD TESTING (100 VIRTUAL USERS)
  if (targetFilter === 'all' || targetFilter === 'load') {
    console.log('---------------------------------------------------------------------');
    console.log(' [3/3] Executing Baseline Load Benchmark (100 Virtual Users, 1 Minute)');
    console.log('---------------------------------------------------------------------');
    loadResult = await runLoadSuite(60, (progress) => {
      const pct = Math.round((progress.sec / progress.totalDuration) * 100);
      console.log(`   Load Bench: [${pct}%] Elapsed ${progress.sec}s/60s | RPS: ${progress.currentRps} req/s | Avg Latency: ${progress.currentAvgLatency}ms`);
    });
    console.log(` Load Benchmark Complete: ${loadResult.metrics.rps} req/sec | Avg Latency: ${loadResult.metrics.avgLatency}ms | Availability: ${loadResult.metrics.availability}\n`);
  }

  // UNIFIED EXECUTIVE SCORECARD
  console.log('╔═══════════════════════════════════════════════════════════════════╗');
  console.log('║                   UNIFIED TEST SCORECARD                         ║');
  console.log('╠═══════════════════════════════════════════════════════════════════╣');
  if (webResult) {
    console.log(`║ Web E2E (Selenium)   : ${webResult.passed}/${webResult.total} Passed (${webResult.passRate}) in ${webResult.duration}`.padEnd(68) + '║');
  }
  if (mobileResult) {
    console.log(`║ Mobile E2E (Appium)  : ${mobileResult.passed}/${mobileResult.total} Passed (${mobileResult.passRate}) in ${mobileResult.duration}`.padEnd(68) + '║');
  }
  if (loadResult) {
    console.log(`║ Baseline Load (100 VU): ${loadResult.metrics.rps} req/sec | Avg ${loadResult.metrics.avgLatency}ms | ${loadResult.metrics.availability}`.padEnd(68) + '║');
  }
  console.log('╚═══════════════════════════════════════════════════════════════════╝\n');

  // Generate Master Unified Excel Report
  generateUnifiedExcelReport({
    web: webResult,
    mobile: mobileResult,
    load: loadResult
  }, UNIFIED_REPORT_PATH);

  console.log(`\nMaster Unified Excel Analysis Report Generated!`);
  console.log(`Location: ${UNIFIED_REPORT_PATH}`);
}

main().catch(console.error);

