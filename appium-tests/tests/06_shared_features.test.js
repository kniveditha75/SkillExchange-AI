/**
 * Mobile E2E Test Suite 06: Shared Features (Chat, Notifications, Profile, Logout)
 */
const { expect } = require('chai');
const { SharedScreen } = require('../screens/AppScreens');

describe('Mobile Suite 6: Shared App Features & User Session', function () {
  this.timeout(60000);
  let shared;

  before(function () {
    shared = new SharedScreen(global.driver);
  });

  it('TC-MOB-036: Navigates to Chat tab and loads active conversations', async function () {
    await shared.tap(shared.chatTab);
  });

  it('TC-MOB-037: Sends real-time chat message in conversation thread', async function () {
    await shared.sendChatMessage('Hello, reviewing your proposal now. Can we schedule a sync?');
  });

  it('TC-MOB-038: Navigates to Notification Center and displays unread badges', async function () {
    await shared.checkNotifications();
  });

  it('TC-MOB-039: Opens Profile Screen and displays user avatar and details', async function () {
    await shared.tap(shared.profileTab);
  });

  it('TC-MOB-040: Executes Logout and confirms session termination back to Welcome screen', async function () {
    await shared.logout();
  });
});

