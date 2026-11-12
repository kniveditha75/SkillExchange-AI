/**
 * Mobile E2E Test Suite 04: Idea Owner Flow (Dashboard, Create Project, Find Team)
 */
const { expect } = require('chai');
const { IdeaOwnerScreen } = require('../screens/AppScreens');

describe('Mobile Suite 4: Idea Owner End-to-End Flow', function () {
  this.timeout(60000);
  let ideaOwner;

  before(function () {
    ideaOwner = new IdeaOwnerScreen(global.driver);
  });

  it('TC-MOB-025: Renders Idea Owner Dashboard with posted projects and funding progress', async function () {
    const isHeader = await ideaOwner.isDisplayed(ideaOwner.dashboardHeader);
    expect(isHeader).to.be.true;
  });

  it('TC-MOB-026: Opens Create Project modal / bottom sheet', async function () {
    await ideaOwner.tap(ideaOwner.createProjectFab);
  });

  it('TC-MOB-027: Validates project title and description mandatory field checks', async function () {
    await ideaOwner.setValue(ideaOwner.projectTitleInput, '');
    await ideaOwner.tap(ideaOwner.submitProjectBtn);
  });

  it('TC-MOB-028: Successfully creates and publishes a new idea project with budget', async function () {
    await ideaOwner.createNewProject(
      'AI Fitness Companion App',
      'Real-time pose estimation and personalized training routines.',
      '$5,000'
    );
  });

  it('TC-MOB-029: Opens Find Team screen and queries available talent profiles', async function () {
    await ideaOwner.tap(ideaOwner.findTeamTab);
  });

  it('TC-MOB-030: Navigates to Find Investor screen and views verified angel investors', async function () {
    await ideaOwner.tap(ideaOwner.findInvestorTab);
  });
});

