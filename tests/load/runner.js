const config = require('./config/load-test.config');
const LoadEngine = require('./engine/load-engine');

async function runLoadSuite(durationOverride, onProgress) {
  const duration = durationOverride || config.durationSeconds;
  const engine = new LoadEngine({
    ...config,
    durationSeconds: duration
  });

  const result = await engine.runBenchmark((progress) => {
    if (onProgress && (progress.sec % 10 === 0 || progress.sec === duration)) {
      onProgress(progress);
    }
  });

  return result;
}

module.exports = {
  runLoadSuite
};

