/**
 * Appium UiAutomator2 Configuration for Android Mobile Testing
 * Target App: SkillExchange AI Android Application (com.simats.skillexchange)
 */
module.exports = {
  server: {
    host: process.env.APPIUM_HOST || '127.0.0.1',
    port: parseInt(process.env.APPIUM_PORT, 10) || 4723,
    path: '/'
  },
  capabilities: {
    platformName: 'Android',
    'appium:automationName': 'UiAutomator2',
    'appium:deviceName': process.env.ANDROID_DEVICE_NAME || 'Android Emulator',
    'appium:appPackage': 'com.simats.skillexchange',
    'appium:appActivity': 'com.simats.skillexchange.MainActivity',
    'appium:noReset': false,
    'appium:fullReset': false,
    'appium:autoGrantPermissions': true,
    'appium:newCommandTimeout': 120,
    'appium:adbExecTimeout': 40000,
    'appium:uiautomator2ServerInstallTimeout': 60000
  },
  testUser: {
    email: 'freelancer@simats.edu',
    password: 'Password@123',
    name: 'Alex Johnson',
    role: 'FREELANCER'
  },
  ideaOwnerUser: {
    email: 'ideaowner@simats.edu',
    password: 'Password@123',
    name: 'Sarah Connor',
    role: 'IDEA_OWNER'
  },
  investorUser: {
    email: 'investor@simats.edu',
    password: 'Password@123',
    name: 'David Vance',
    role: 'INVESTOR'
  }
};

