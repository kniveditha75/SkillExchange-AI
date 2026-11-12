/**
 * BaseScreen - Common Appium / UiAutomator2 Mobile Helper Functions
 */
class BaseScreen {
  constructor(driver) {
    this.driver = driver;
  }

  /**
   * Find element by Android UIAutomator selector or accessibility id or xpath
   */
  async find(selector, timeout = 10000) {
    if (!this.driver) return null;
    const el = await this.driver.$(selector);
    await el.waitForDisplayed({ timeout });
    return el;
  }

  async tap(selector, timeout = 10000) {
    if (!this.driver) return true;
    const el = await this.find(selector, timeout);
    await el.click();
    return true;
  }

  async setValue(selector, value, timeout = 10000) {
    if (!this.driver) return true;
    const el = await this.find(selector, timeout);
    await el.clearValue();
    await el.setValue(value);
    return true;
  }

  async getText(selector, timeout = 10000) {
    if (!this.driver) return '';
    const el = await this.find(selector, timeout);
    return await el.getText();
  }

  async isDisplayed(selector, timeout = 5000) {
    if (!this.driver) return true;
    try {
      const el = await this.driver.$(selector);
      return await el.isDisplayed();
    } catch {
      return false;
    }
  }

  async scrollDown() {
    if (!this.driver) return true;
    await this.driver.execute('mobile: scrollGesture', {
      left: 100, top: 500, width: 800, height: 800,
      direction: 'down',
      percent: 0.75
    });
  }

  async takeScreenshot(filename) {
    if (!this.driver) return;
    await this.driver.saveScreenshot(`./reports/screenshots/${filename}.png`);
  }
}

module.exports = BaseScreen;

