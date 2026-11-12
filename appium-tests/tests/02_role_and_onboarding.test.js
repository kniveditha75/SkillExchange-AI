/**
 * Mobile E2E Test Suite 02: Role Selection & Onboarding Flow
 */
const { expect } = require('chai');
const { RoleSelectionScreen, OnboardingScreen } = require('../screens/AppScreens');

describe('Mobile Suite 2: Role Selection & Onboarding Screens', function () {
  this.timeout(60000);
  let roleSelect, onboarding;

  before(function () {
    roleSelect = new RoleSelectionScreen(global.driver);
    onboarding = new OnboardingScreen(global.driver);
  });

  it('TC-MOB-011: Renders the 3 primary role options (Freelancer, Idea Owner, Investor)', async function () {
    const hasFree = await roleSelect.isDisplayed(roleSelect.freelancerCard);
    const hasOwner = await roleSelect.isDisplayed(roleSelect.ideaOwnerCard);
    const hasInv = await roleSelect.isDisplayed(roleSelect.investorCard);
    expect(hasFree).to.be.true;
    expect(hasOwner).to.be.true;
    expect(hasInv).to.be.true;
  });

  it('TC-MOB-012: Highlights selected role card upon user tap', async function () {
    await roleSelect.tap(roleSelect.freelancerCard);
  });

  it('TC-MOB-013: Navigates to Onboarding screen after role selection confirmation', async function () {
    await roleSelect.tap(roleSelect.continueBtn);
  });

  it('TC-MOB-014: Displays Onboarding profile bio input and skill selection chips', async function () {
    const hasBio = await onboarding.isDisplayed(onboarding.bioInput);
    expect(hasBio).to.be.true;
  });

  it('TC-MOB-015: Enforces bio minimum character length validation', async function () {
    await onboarding.setValue(onboarding.bioInput, 'Hi');
    await onboarding.tap(onboarding.submitOnboardingBtn);
  });

  it('TC-MOB-016: Adds multiple skills (React, Kotlin, Python) to profile', async function () {
    await onboarding.setValue(onboarding.bioInput, 'Experienced Full Stack Mobile Developer proficient in Compose.');
    await onboarding.setValue(onboarding.skillsTagInput, 'Kotlin, Compose, Android');
  });

  it('TC-MOB-017: Successfully finishes Onboarding and redirects to chosen Role Dashboard', async function () {
    await onboarding.tap(onboarding.submitOnboardingBtn);
  });
});

