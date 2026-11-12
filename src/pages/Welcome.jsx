import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { 
  Zap, 
  ArrowRight, 
  Code, 
  Lightbulb, 
  TrendingUp, 
  Sparkles, 
  Users, 
  ShieldCheck, 
  CheckCircle2,
  Cpu
} from 'lucide-react';

const Welcome = () => {
  const navigate = useNavigate();

  const handleGetStarted = () => {
    navigate('/login');
  };

  return (
    <div className="welcome-container">
      {/* Navigation Bar */}
      <nav className="welcome-navbar">
        <div className="welcome-navbar-content">
          <Link to="/" className="welcome-navbar-logo">
            <Zap size={28} fill="#6366f1" />
            <span>SkillExchange AI</span>
          </Link>
          <div className="welcome-navbar-actions">
            <Link to="/login" className="btn btn-secondary btn-sm">
              Sign In
            </Link>
            <Link to="/signup" className="btn btn-primary btn-sm">
              Get Started
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <header className="welcome-hero">
        <div className="welcome-hero-tag">
          <Sparkles size={16} />
          <span>Next-Generation AI Collaboration Platform</span>
        </div>
        <h1 className="welcome-hero-title">
          Where <span>Skills</span> Meet <span>Vision</span> & <span>Capital</span>
        </h1>
        <p className="welcome-hero-desc">
          An AI-powered decentralized ecosystem for builders, creators, and backers. 
          Exchange professional skills, assemble elite startup teams, and source investment.
        </p>
        <div className="welcome-hero-cta">
          <button onClick={handleGetStarted} className="btn btn-primary">
            Get Started
            <ArrowRight size={18} />
          </button>
          <a href="#features" className="btn btn-secondary">
            Explore Features
          </a>
        </div>
      </header>

      {/* Three Core Pillars Section */}
      <section id="features" className="welcome-section">
        <div className="welcome-section-header">
          <h2 className="welcome-section-title">One Ecosystem. Three Core Pillars.</h2>
          <p className="welcome-section-subtitle">
            Whether you want to write code, design experiences, scale a product, or back high-growth ideas, we have a specialized workspace built for you.
          </p>
        </div>

        <div className="welcome-pillars-grid">
          {/* Pillar 1: Freelancer */}
          <div className="welcome-pillar-card freelancer">
            <div className="welcome-pillar-icon">
              <Code size={28} />
            </div>
            <h3 className="welcome-pillar-title">Developers & Creators</h3>
            <p className="welcome-pillar-desc">
              Swap expertise directly with peer builders, find high-priority tasks, and level up your portfolio.
            </p>
            <ul className="welcome-pillar-features">
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-purple" />
                <span>AI Skill Verification & Gap Analysis</span>
              </li>
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-purple" />
                <span>Direct Peer-to-Peer Skill Swapping</span>
              </li>
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-purple" />
                <span>Verified Portfolios & Credentialing</span>
              </li>
            </ul>
          </div>

          {/* Pillar 2: Idea Owner */}
          <div className="welcome-pillar-card ideaowner">
            <div className="welcome-pillar-icon">
              <Lightbulb size={28} />
            </div>
            <h3 className="welcome-pillar-title">Idea Owners & Startups</h3>
            <p className="welcome-pillar-desc">
              Turn abstract concepts into viable products. Build teams, manage tasks, and align with investors.
            </p>
            <ul className="welcome-pillar-features">
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-green" />
                <span>Co-Founder & Talent Matchmaking</span>
              </li>
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-green" />
                <span>Equity & Task Incentive Pools</span>
              </li>
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-green" />
                <span>Interactive Pitch Deck Creators</span>
              </li>
            </ul>
          </div>

          {/* Pillar 3: Investor */}
          <div className="welcome-pillar-card investor">
            <div className="welcome-pillar-icon">
              <TrendingUp size={28} />
            </div>
            <h3 className="welcome-pillar-title">Investors & Backers</h3>
            <p className="welcome-pillar-desc">
              Discover vetted opportunities early. Monitor project progress, review code commits, and deploy funding safely.
            </p>
            <ul className="welcome-pillar-features">
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-pink" />
                <span>Real-Time Traction Dashboards</span>
              </li>
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-pink" />
                <span>Milestone-Locked Smart Funding</span>
              </li>
              <li className="welcome-pillar-feature-item">
                <CheckCircle2 size={16} className="badge-pink" />
                <span>Interactive Venture Portfolios</span>
              </li>
            </ul>
          </div>
        </div>
      </section>

      {/* AI Synergy Section */}
      <section className="welcome-section">
        <div className="welcome-ai-showcase">
          <div className="welcome-ai-text">
            <span className="welcome-ai-badge">AI Integration</span>
            <h2 className="welcome-ai-title">Intelligent Matchmaking & Gap Analysis</h2>
            <p className="welcome-ai-desc">
              Our advanced AI model analyzes repository commits, UI/UX portfolios, and startup pitch materials to calculate precision synergy scores. It auto-recommends roles, verifies skill capabilities, and generates risk reports for venture backers.
            </p>
            <div className="welcome-ai-bullets">
              <div className="welcome-ai-bullet">
                <Cpu size={18} style={{ color: 'var(--accent-purple)' }} />
                <span>Predictive Synergy Scoring</span>
              </div>
              <div className="welcome-ai-bullet">
                <Users size={18} style={{ color: 'var(--accent-green)' }} />
                <span>Autopopulated Team Assembly</span>
              </div>
              <div className="welcome-ai-bullet">
                <ShieldCheck size={18} style={{ color: 'var(--accent-pink)' }} />
                <span>Smart Verification Audits</span>
              </div>
              <div className="welcome-ai-bullet">
                <Sparkles size={18} style={{ color: 'var(--accent-yellow)' }} />
                <span>Smart Contract Milestone Triggering</span>
              </div>
            </div>
          </div>
          <div className="welcome-ai-visual">
            <div className="ai-orb-glow">
              <div className="ai-orb-center">
                <Cpu size={40} />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Bottom Call to Action */}
      <section className="welcome-bottom-cta">
        <h2 className="welcome-bottom-title">Ready to build the future?</h2>
        <p className="welcome-bottom-desc">
          Join thousands of developers, entrepreneurs, and investors currently exchanging value in the SkillExchange AI ecosystem.
        </p>
        <button onClick={handleGetStarted} className="btn btn-primary">
          Enter Platform Now
          <ArrowRight size={18} />
        </button>
      </section>

      {/* Footer */}
      <footer className="welcome-footer-panel">
        <ul className="welcome-footer-links">
          <li><Link to="/login">Login</Link></li>
          <li><Link to="/signup">Register</Link></li>
          <li><a href="#features">Features</a></li>
          <li><a href="#">Security</a></li>
          <li><a href="#">Privacy Policy</a></li>
        </ul>
        <p>&copy; {new Date().getFullYear()} SkillExchange AI. All rights reserved. Empowered by Gemini.</p>
      </footer>
    </div>
  );
};

export default Welcome;
