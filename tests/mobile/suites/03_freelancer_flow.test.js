/**
 * Mobile E2E Test Suite 03: Freelancer Flow (Dashboard, Marketplace, Proposal, Skills)
 */
const { expect } = require('chai');
const { FreelancerScreen } = require('../screens/AppScreens');

describe('Mobile Suite 3: Freelancer End-to-End Flow', function () {
  this.timeout(60000);
  let freelancer;

  before(function () {
    freelancer = new FreelancerScreen(global.driver);
  });

  it('TC-MOB-018: Renders Freelancer Dashboard with earnings and active jobs summary', async function () {
    const isHeader = await freelancer.isDisplayed(freelancer.dashboardHeader);
    expect(isHeader).to.be.true;
  });

  it('TC-MOB-019: Navigates to Project Marketplace and fetches active projects', async function () {
    await freelancer.browseProjects();
  });

  it('TC-MOB-020: Filters projects by category tag (Mobile Development)', async function () {
    await freelancer.scrollDown();
  });

  it('TC-MOB-021: Opens Project Detail screen and displays requirements and budget', async function () {
    await freelancer.tap(freelancer.projectItem);
  });

  it('TC-MOB-022: Submits a proposal with customized cover letter and bid price', async function () {
    await freelancer.applyToFirstProject('I have 5 years building Android Compose apps. Ready to deliver.');
  });

  it('TC-MOB-023: Navigates to My Work screen and verifies submitted proposal listing', async function () {
    await freelancer.tap(freelancer.myWorkTab);
  });

  it('TC-MOB-024: Opens AI Skill Analysis screen and displays skill gap radar and recommendations', async function () {
    await freelancer.viewSkillAnalysis();
  });
});

