import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../../services/api';
import { Search, Filter, Briefcase, ChevronRight } from 'lucide-react';

const ProjectMarketplace = () => {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [seekingFunding, setSeekingFunding] = useState(null);
  const navigate = useNavigate();

  const categories = [
    { label: 'All Projects', value: '' },
    { label: 'AI & Machine Learning', value: 'AI/ML' },
    { label: 'Mobile Apps', value: 'Mobile' },
    { label: 'Web Apps', value: 'Web' },
    { label: 'Design & UI/UX', value: 'Design' },
    { label: 'Finance & Business', value: 'Finance' },
  ];

  const fetchProjects = async () => {
    setLoading(true);
    try {
      const params = {
        status: 'open',
      };
      if (searchQuery) params.q = searchQuery;
      if (selectedCategory) params.category = selectedCategory;
      if (seekingFunding !== null) params.seeking_funding = seekingFunding;

      const data = await apiService.listProjects(params);
      setProjects(data);
    } catch (error) {
      console.error("Failed to load projects:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjects();
  }, [selectedCategory, seekingFunding]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    fetchProjects();
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Project Marketplace</h1>
          <p className="page-subtitle">Find collaborative projects and apply your skills.</p>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="glass-panel" style={{ padding: '20px', marginBottom: '30px', display: 'flex', flexWrap: 'wrap', gap: '16px', alignItems: 'center' }}>
        <form onSubmit={handleSearchSubmit} style={{ flex: 1, minWidth: '280px', display: 'flex', gap: '8px' }}>
          <div style={{ position: 'relative', flex: 1 }}>
            <Search size={18} style={{ position: 'absolute', left: '16px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input 
              type="text" 
              className="form-input" 
              style={{ paddingLeft: '44px' }}
              placeholder="Search by keywords, title, skills..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-primary">Search</button>
        </form>

        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <button 
            className={`btn btn-secondary btn-sm ${seekingFunding === true ? 'btn-outline' : ''}`}
            onClick={() => setSeekingFunding(prev => prev === true ? null : true)}
          >
            Seeking Funding
          </button>
        </div>
      </div>

      {/* Category Tabs */}
      <div className="tabs-header" style={{ marginBottom: '30px' }}>
        {categories.map((cat) => (
          <button 
            key={cat.label} 
            className={`tab-btn ${selectedCategory === cat.value ? 'active' : ''}`}
            onClick={() => setSelectedCategory(cat.value)}
          >
            {cat.label}
          </button>
        ))}
      </div>

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '50px 0' }}>
          <div style={{ border: '3px solid var(--border-color)', borderTop: '3px solid var(--accent-purple)', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
        </div>
      ) : projects.length === 0 ? (
        <div className="card glass-panel" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <Briefcase size={40} style={{ margin: '0 auto 16px', opacity: 0.5 }} />
          <h3>No projects found</h3>
          <p style={{ fontSize: '0.9rem', marginTop: '8px' }}>Try resetting your search query or choosing another category filter.</p>
        </div>
      ) : (
        <div className="dashboard-grid">
          {projects.map((project) => (
            <div key={project.id} className="card glass-panel">
              <div className="card-header">
                <div>
                  <span className="badge badge-purple" style={{ marginBottom: '8px', fontSize: '0.65rem' }}>{project.category}</span>
                  <h3 className="card-title">{project.title}</h3>
                </div>
                {project.seeking_funding && (
                  <span className="badge badge-green" style={{ fontSize: '0.65rem' }}>Funding Goal</span>
                )}
              </div>

              <p className="card-desc">{project.description}</p>

              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '8px', fontWeight: 600 }}>Required Skills:</div>
                <div className="chip-container">
                  {project.required_skills ? (
                    project.required_skills.split(',').map((skill) => (
                      <span key={skill} className="chip" style={{ padding: '3px 8px', fontSize: '0.75rem' }}>
                        {skill.trim()}
                      </span>
                    ))
                  ) : (
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>None</span>
                  )}
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border-color)', paddingTop: '16px', marginTop: 'auto' }}>
                <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                  By: <strong>{project.owner_name || 'Anonymous'}</strong>
                </span>
                <button 
                  className="btn btn-secondary btn-sm"
                  onClick={() => navigate(`/freelancer/projects/${project.id}`)}
                >
                  View details <ChevronRight size={14} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ProjectMarketplace;
