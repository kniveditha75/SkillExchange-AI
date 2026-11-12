const path = require('path');
const config = require('./config/load-test.config');
const LoadEngine = require('./engine/load-engine');
const { generateLoadTestExcelReport } = require('./reporters/excel-reporter');

const REPORT_PATH = path.join(__dirname, 'reports', 'baseline-load-test-analysis.xlsx');

// Parse CLI flags
const args = process.argv.slice(2);
let duration = config.durationSeconds;
const durationIdx = args.indexOf('--duration');
if (durationIdx !== -1 && args[durationIdx + 1]) {
  duration = parseInt(args[durationIdx + 1], 10) || 60;
}

async function run() {
  console.log('================================================================');
  console.log(' SkillExchange AI — Baseline & Concurrency Load Test');
  console.log('================================================================');
  console.log(`Target Service : ${config.targetUrl}`);
  console.log(`Virtual Users  : ${config.virtualUsers} concurrent users`);
  console.log(`Duration       : ${duration} seconds (continuous run)`);
  console.log(`Report Output  : ${REPORT_PATH}\n`);

  const engine = new LoadEngine({
    ...config,
    durationSeconds: duration
  });

  console.log('--- Benchmarking Virtual User Traffic (100 VUs) ---');
  const result = await engine.runBenchmark((progress) => {
    if (progress.sec % 10 === 0 || progress.sec === duration) {
      const pct = Math.round((progress.sec / progress.totalDuration) * 100);
      console.log(` [Elapsed: ${progress.sec}s / ${progress.totalDuration}s (${pct}%)] RPS: ${progress.currentRps} req/s | Avg Latency: ${progress.currentAvgLatency}ms | Total Sent: ${progress.totalDispatched}`);
    }
  });

  const m = result.metrics;

  console.log('\n================================================================');
  console.log(' BASELINE LOAD TEST RESULTS SUMMARY');
  console.log('================================================================');
  console.log(` Concurrent VUs       : ${m.virtualUsers}`);
  console.log(` Duration             : ${m.durationSeconds}s`);
  console.log(` Total Requests       : ${m.totalRequests}`);
  console.log(` -----------------------------------------------------------`);
  console.log(` Requests Per Second  : ${m.rps} req/sec`);
  console.log(` -----------------------------------------------------------`);
  console.log(` Response Time (Avg)  : ${m.avgLatency} ms`);
  console.log(` Response Time (Min)  : ${m.minLatency} ms  (Fastest Response)`);
  console.log(` Response Time (Max)  : ${m.maxLatency} ms  (Slowest Response)`);
  console.log(` Response Time (P95)  : ${m.p95Latency} ms`);
  console.log(` Response Time (P99)  : ${m.p99Latency} ms`);
  console.log(` -----------------------------------------------------------`);
  console.log(` Success Rate         : ${m.availability} (${m.successRequests} passed / ${m.failedRequests} errors)`);
  console.log('================================================================\n');

  // Generate the Excel Analysis Report
  generateLoadTestExcelReport(m, result.timeSeries, result.endpointData, REPORT_PATH);

  console.log(`\nBaseline Load Test Excel Analysis Report generated successfully!`);
  console.log(`Open: ${REPORT_PATH}`);
}

run().catch(console.error);

