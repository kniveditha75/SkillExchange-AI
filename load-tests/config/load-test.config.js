/**
 * Baseline & Load Testing Configuration
 * SkillExchange AI Backend
 */
module.exports = {
  // Target Backend
  targetUrl: process.env.TARGET_URL || 'http://localhost:8000',

  // Concurrency & Duration
  virtualUsers: 100,            // 100 concurrent virtual users
  durationSeconds: 60,          // Running continuously for 1 minute

  // Endpoints to benchmark
  endpoints: [
    { name: 'Health Check', path: '/', method: 'GET', weight: 15 },
    { name: 'Project Marketplace', path: '/projects', method: 'GET', weight: 30 },
    { name: 'User Authentication', path: '/auth/login', method: 'POST', weight: 20 },
    { name: 'Notifications Center', path: '/notifications', method: 'GET', weight: 15 },
    { name: 'Investment Deals', path: '/funding', method: 'GET', weight: 10 },
    { name: 'User Profile Query', path: '/users/profile', method: 'GET', weight: 10 }
  ],

  // Service Level Agreements (SLAs)
  sla: {
    minRps: 100,                // Expecting >= 100 requests/sec
    maxAvgLatencyMs: 300,       // Expected average latency <= 300ms
    maxLatencyMs: 1500,         // Slowest acceptable response <= 1.5s (1500ms)
    minLatencyMs: 40,           // Fastest response ~ 40-50ms
    maxErrorRatePct: 1.0        // Max allowable error rate <= 1.0%
  }
};

