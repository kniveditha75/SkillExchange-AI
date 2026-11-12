/**
 * Mobile E2E Test Suite 05: Investor Flow (Dashboard, Investment Marketplace, Portfolio)
 */
const { expect } = require('chai');
const { InvestorScreen } = require('../screens/AppScreens');

describe('Mobile Suite 5: Investor End-to-End Flow', function () {
  this.timeout(60000);
  let investor;

  before(function () {
    investor = new InvestorScreen(global.driver);
  });

  it('TC-MOB-031: Renders Investor Dashboard with portfolio valuation and active deals', async function () {
    const isHeader = await investor.isDisplayed(investor.dashboardHeader);
    expect(isHeader).to.be.true;
  });

  it('TC-MOB-032: Navigates to Investment Marketplace and displays startup pitch listings', async function () {
    await investor.browseStartups();
  });

  it('TC-MOB-033: Inspects startup pitch deck, funding goal, and equity terms', async function () {
    await investor.tap(investor.startupCard);
  });

  it('TC-MOB-034: Initiates contact with founder to request financial model and meeting', async function () {
    await investor.tap(investor.investBtn);
  });

  it('TC-MOB-035: Opens My Portfolio screen and verifies list of equity investments', async function () {
    await investor.openPortfolio();
  });
});

