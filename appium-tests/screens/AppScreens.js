const BaseScreen = require('./BaseScreen');

class SplashScreen extends BaseScreen {
  get splashLogo() { return 'android=new UiSelector().textContains("SkillExchange")'; }
  get getStartedBtn() { return 'android=new UiSelector().textMatches("(?i)Get Started|Start|Continue")'; }
  get loginRedirectBtn() { return 'android=new UiSelector().textMatches("(?i)Sign In|Login")'; }

  async waitForSplash() {
    return await this.isDisplayed(this.splashLogo, 8000);
  }

  async clickGetStarted() {
    return await this.tap(this.getStartedBtn);
  }

  async clickSignIn() {
    return await this.tap(this.loginRedirectBtn);
  }
}

class AuthScreen extends BaseScreen {
  get emailInput() { return 'android=new UiSelector().className("android.widget.EditText").instance(0)'; }
  get passwordInput() { return 'android=new UiSelector().className("android.widget.EditText").instance(1)'; }
  get nameInput() { return 'android=new UiSelector().className("android.widget.EditText").textContains("Name")'; }
  get loginBtn() { return 'android=new UiSelector().textMatches("(?i)Log In|Sign In")'; }
  get signupBtn() { return 'android=new UiSelector().textMatches("(?i)Sign Up|Register|Create Account")'; }
  get togglePasswordVisibility() { return 'android=new UiSelector().descriptionContains("Show password")'; }
  get errorMessage() { return 'android=new UiSelector().textContains("Invalid|Error|Required")'; }
  get goToSignupLink() { return 'android=new UiSelector().textContains("Don\'t have an account")'; }
  get goToLoginLink() { return 'android=new UiSelector().textContains("Already have an account")'; }

  async login(email, password) {
    await this.setValue(this.emailInput, email);
    await this.setValue(this.passwordInput, password);
    await this.tap(this.loginBtn);
  }

  async signup(name, email, password) {
    if (await this.isDisplayed(this.nameInput)) {
      await this.setValue(this.nameInput, name);
    }
    await this.setValue(this.emailInput, email);
    await this.setValue(this.passwordInput, password);
    await this.tap(this.signupBtn);
  }
}

class RoleSelectionScreen extends BaseScreen {
  get freelancerCard() { return 'android=new UiSelector().textContains("Freelancer")'; }
  get ideaOwnerCard() { return 'android=new UiSelector().textContains("Idea Owner")'; }
  get investorCard() { return 'android=new UiSelector().textContains("Investor")'; }
  get continueBtn() { return 'android=new UiSelector().textMatches("(?i)Continue|Select|Proceed")'; }

  async selectFreelancer() {
    await this.tap(this.freelancerCard);
    await this.tap(this.continueBtn);
  }

  async selectIdeaOwner() {
    await this.tap(this.ideaOwnerCard);
    await this.tap(this.continueBtn);
  }

  async selectInvestor() {
    await this.tap(this.investorCard);
    await this.tap(this.continueBtn);
  }
}

class OnboardingScreen extends BaseScreen {
  get bioInput() { return 'android=new UiSelector().className("android.widget.EditText").instance(0)'; }
  get skillsTagInput() { return 'android=new UiSelector().textContains("Skills|Add Skill")'; }
  get submitOnboardingBtn() { return 'android=new UiSelector().textMatches("(?i)Finish|Complete|Submit|Next")'; }

  async fillOnboarding(bio, skill) {
    await this.setValue(this.bioInput, bio);
    if (await this.isDisplayed(this.skillsTagInput)) {
      await this.setValue(this.skillsTagInput, skill);
    }
    await this.tap(this.submitOnboardingBtn);
  }
}

class FreelancerScreen extends BaseScreen {
  get dashboardHeader() { return 'android=new UiSelector().textContains("Freelancer Dashboard|Find Work")'; }
  get marketplaceTab() { return 'android=new UiSelector().textMatches("(?i)Marketplace|Projects|Browse")'; }
  get projectItem() { return 'android=new UiSelector().resourceIdMatches(".*project_item.*|.*card.*")'; }
  get applyBtn() { return 'android=new UiSelector().textMatches("(?i)Apply|Submit Proposal")'; }
  get proposalTextInput() { return 'android=new UiSelector().className("android.widget.EditText")'; }
  get skillAnalysisTab() { return 'android=new UiSelector().textMatches("(?i)Skill Analysis|Skills")'; }
  get myWorkTab() { return 'android=new UiSelector().textMatches("(?i)My Work|Proposals")'; }

  async browseProjects() {
    await this.tap(this.marketplaceTab);
  }

  async applyToFirstProject(proposalText) {
    await this.tap(this.projectItem);
    await this.tap(this.applyBtn);
    if (await this.isDisplayed(this.proposalTextInput)) {
      await this.setValue(this.proposalTextInput, proposalText);
      await this.tap(this.applyBtn);
    }
  }

  async viewSkillAnalysis() {
    await this.tap(this.skillAnalysisTab);
  }
}

class IdeaOwnerScreen extends BaseScreen {
  get dashboardHeader() { return 'android=new UiSelector().textContains("Idea Owner|My Projects")'; }
  get createProjectFab() { return 'android=new UiSelector().textMatches("(?i)Create|New Project|\\+")'; }
  get projectTitleInput() { return 'android=new UiSelector().className("android.widget.EditText").instance(0)'; }
  get projectDescInput() { return 'android=new UiSelector().className("android.widget.EditText").instance(1)'; }
  get projectBudgetInput() { return 'android=new UiSelector().className("android.widget.EditText").instance(2)'; }
  get submitProjectBtn() { return 'android=new UiSelector().textMatches("(?i)Publish|Create|Submit")'; }
  get findTeamTab() { return 'android=new UiSelector().textMatches("(?i)Find Team|Talent")'; }
  get findInvestorTab() { return 'android=new UiSelector().textMatches("(?i)Find Investor|Funding")'; }

  async createNewProject(title, desc, budget) {
    await this.tap(this.createProjectFab);
    await this.setValue(this.projectTitleInput, title);
    await this.setValue(this.projectDescInput, desc);
    if (await this.isDisplayed(this.projectBudgetInput)) {
      await this.setValue(this.projectBudgetInput, budget);
    }
    await this.tap(this.submitProjectBtn);
  }
}

class InvestorScreen extends BaseScreen {
  get dashboardHeader() { return 'android=new UiSelector().textContains("Investor|Investment Portfolio")'; }
  get marketplaceTab() { return 'android=new UiSelector().textMatches("(?i)Marketplace|Startups|Deals")'; }
  get startupCard() { return 'android=new UiSelector().className("android.widget.TextView").textContains("Startup|Tech|AI")'; }
  get investBtn() { return 'android=new UiSelector().textMatches("(?i)Invest|Contact Founder|Connect")'; }
  get portfolioTab() { return 'android=new UiSelector().textMatches("(?i)Portfolio|Investments")'; }

  async browseStartups() {
    await this.tap(this.marketplaceTab);
  }

  async openPortfolio() {
    await this.tap(this.portfolioTab);
  }
}

class SharedScreen extends BaseScreen {
  get chatTab() { return 'android=new UiSelector().textMatches("(?i)Chat|Messages")'; }
  get chatConversation() { return 'android=new UiSelector().className("android.widget.TextView").instance(1)'; }
  get messageInput() { return 'android=new UiSelector().className("android.widget.EditText")'; }
  get sendBtn() { return 'android=new UiSelector().descriptionContains("Send")'; }
  get notificationsTab() { return 'android=new UiSelector().textMatches("(?i)Notifications|Alerts")'; }
  get profileTab() { return 'android=new UiSelector().textMatches("(?i)Profile|Account")'; }
  get logoutBtn() { return 'android=new UiSelector().textMatches("(?i)Log Out|Sign Out")'; }

  async sendChatMessage(msg) {
    await this.tap(this.chatTab);
    await this.tap(this.chatConversation);
    await this.setValue(this.messageInput, msg);
    await this.tap(this.sendBtn);
  }

  async checkNotifications() {
    await this.tap(this.notificationsTab);
  }

  async logout() {
    await this.tap(this.profileTab);
    await this.tap(this.logoutBtn);
  }
}

module.exports = {
  SplashScreen,
  AuthScreen,
  RoleSelectionScreen,
  OnboardingScreen,
  FreelancerScreen,
  IdeaOwnerScreen,
  InvestorScreen,
  SharedScreen
};

