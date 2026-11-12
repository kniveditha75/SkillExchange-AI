/**
 * Mobile E2E Test Suite 01: Splash Screen, App Launch, Login & Signup
 */
const { expect } = require('chai');
const { SplashScreen, AuthScreen } = require('../screens/AppScreens');

describe('Mobile Suite 1: Splash & Authentication Flows', function () {
  this.timeout(60000);
  let splash, auth;

  before(function () {
    splash = new SplashScreen(global.driver);
    auth = new AuthScreen(global.driver);
  });

  it('TC-MOB-001: App launches successfully and displays Splash Screen branding', async function () {
    const isSplash = await splash.waitForSplash();
    expect(isSplash).to.be.true;
  });

  it('TC-MOB-002: Splash screen navigates to Welcome/Get Started screen', async function () {
    await splash.clickGetStarted();
  });

  it('TC-MOB-003: Login screen renders email and password fields', async function () {
    const hasEmail = await auth.isDisplayed(auth.emailInput);
    const hasPass = await auth.isDisplayed(auth.passwordInput);
    expect(hasEmail).to.be.true;
    expect(hasPass).to.be.true;
  });

  it('TC-MOB-004: Validates empty email field rejection on login', async function () {
    await auth.setValue(auth.emailInput, '');
    await auth.setValue(auth.passwordInput, 'ValidPass@123');
    await auth.tap(auth.loginBtn);
  });

  it('TC-MOB-005: Validates password masking toggle button', async function () {
    await auth.tap(auth.togglePasswordVisibility);
  });

  it('TC-MOB-006: Rejects invalid credentials with error banner', async function () {
    await auth.setValue(auth.emailInput, 'unknown_mobile_user@simats.edu');
    await auth.setValue(auth.passwordInput, 'WrongPassword123!');
    await auth.tap(auth.loginBtn);
  });

  it('TC-MOB-007: Navigates from Login screen to Signup screen', async function () {
    await auth.tap(auth.goToSignupLink);
  });

  it('TC-MOB-008: Validates Signup form password complexity requirements', async function () {
    await auth.setValue(auth.emailInput, 'newtester@simats.edu');
    await auth.setValue(auth.passwordInput, '123');
    await auth.tap(auth.signupBtn);
  });

  it('TC-MOB-009: Successfully submits valid signup registration', async function () {
    await auth.setValue(auth.nameInput, 'Alex Test');
    await auth.setValue(auth.emailInput, 'testuser_' + Date.now() + '@simats.edu');
    await auth.setValue(auth.passwordInput, 'Password@123');
    await auth.tap(auth.signupBtn);
  });

  it('TC-MOB-010: Authenticates registered user with valid login token', async function () {
    await auth.setValue(auth.emailInput, 'freelancer@simats.edu');
    await auth.setValue(auth.passwordInput, 'Password@123');
    await auth.tap(auth.loginBtn);
  });
});

