import React, { useState, useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import apiService from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { Send, Sparkles, MessageSquare, AlertCircle, RefreshCw } from 'lucide-react';

const Chat = () => {
  const { user } = useAuth();
  const location = useLocation();
  const [conversations, setConversations] = useState([]);
  const [activeConvId, setActiveConvId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [highlights, setHighlights] = useState([]);
  
  const [loadingConvs, setLoadingConvs] = useState(true);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [loadingHighlights, setLoadingHighlights] = useState(false);
  
  const messagesEndRef = useRef(null);
  const pollingInterval = useRef(null);

  // Load conversations initially
  const loadConversations = async (selectId = null) => {
    try {
      const data = await apiService.listConversations();
      setConversations(data);
      
      // If we are directed to select a specific conversation
      if (selectId) {
        setActiveConvId(selectId);
      } else if (data.length > 0 && !activeConvId) {
        // Default to first conversation
        setActiveConvId(data[0].id);
      }
    } catch (err) {
      console.error("Failed to load conversations:", err);
    } finally {
      setLoadingConvs(false);
    }
  };

  useEffect(() => {
    const directConvId = location.state?.conversationId;
    loadConversations(directConvId);

    // Refresh conversation list every 10 seconds
    const interval = setInterval(() => loadConversations(activeConvId), 10000);
    return () => clearInterval(interval);
  }, [location.state, activeConvId]);

  // Load messages whenever active conversation changes
  useEffect(() => {
    if (!activeConvId) return;

    const fetchMessages = async () => {
      try {
        const msgs = await apiService.getMessages(activeConvId);
        setMessages(msgs);
      } catch (err) {
        console.error("Failed to fetch messages:", err);
      }
    };

    fetchMessages();
    fetchHighlights();

    // Set up polling for messages (every 2 seconds)
    if (pollingInterval.current) clearInterval(pollingInterval.current);
    pollingInterval.current = setInterval(async () => {
      try {
        // Refetch messages to get updates
        const msgs = await apiService.getMessages(activeConvId);
        setMessages(msgs);
      } catch (err) {
        console.error("Polling messages failed:", err);
      }
    }, 2000);

    return () => {
      if (pollingInterval.current) clearInterval(pollingInterval.current);
    };
  }, [activeConvId]);

  // Scroll to bottom when message log changes
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const fetchHighlights = async () => {
    if (!activeConvId) return;
    setLoadingHighlights(true);
    try {
      const data = await apiService.getChatHighlights(activeConvId);
      setHighlights(data.highlights || []);
    } catch (err) {
      console.error("Failed to load chat highlights:", err);
    } finally {
      setLoadingHighlights(false);
    }
  };

  const handleSendMessage = async (e) => {
    e.preventDefault();
    const text = newMessage.trim();
    if (!text || !activeConvId) return;

    setNewMessage('');
    try {
      const sent = await apiService.sendMessage(activeConvId, { content: text });
      setMessages(prev => [...prev, sent]);
      // Reload highlights after message sent
      fetchHighlights();
    } catch (err) {
      console.error("Failed to send message:", err);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Collaboration Conversations</h1>
          <p className="page-subtitle">Coordinate milestones and sync up with team members.</p>
        </div>
      </div>

      <div className="chat-window glass-panel">
        
        {/* Conversations Sidebar List */}
        <div className="chat-sidebar">
          <div style={{ padding: '16px', borderBottom: '1px solid var(--border-color)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px' }}>
            <MessageSquare size={18} /> Conversations
          </div>

          {loadingConvs ? (
            <div style={{ display: 'flex', justifyContent: 'center', padding: '30px 0' }}>
              <div style={{ border: '2px solid var(--border-color)', borderTop: '2px solid var(--accent-purple)', borderRadius: '50%', width: '20px', height: '20px', animation: 'spin 1s linear infinite' }}></div>
            </div>
          ) : conversations.length === 0 ? (
            <div style={{ padding: '30px', textAlign: 'center', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
              No chats active yet. Join teams or apply to collaborate.
            </div>
          ) : (
            <ul className="chat-list">
              {conversations.map((conv) => (
                <li 
                  key={conv.id} 
                  className={`chat-item ${activeConvId === conv.id ? 'active' : ''}`}
                  onClick={() => setActiveConvId(conv.id)}
                >
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontWeight: 600, fontSize: '0.9rem', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      {conv.title || `Chat Room #${conv.id}`}
                    </div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', marginTop: '4px' }}>
                      {conv.last_message || 'No messages yet'}
                    </div>
                  </div>
                  {conv.unread_count > 0 && (
                    <span 
                      style={{ 
                        background: 'var(--accent-pink)', 
                        color: 'white', 
                        fontSize: '0.7rem', 
                        fontWeight: 600, 
                        borderRadius: '50%', 
                        width: '18px', 
                        height: '18px', 
                        display: 'flex', 
                        alignItems: 'center', 
                        justifyContent: 'center' 
                      }}
                    >
                      {conv.unread_count}
                    </span>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Selected Chat Box Area */}
        {activeConvId ? (
          <div className="chat-area">
            <div className="chat-messages">
              {messages.length === 0 ? (
                <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', color: 'var(--text-secondary)', gap: '8px' }}>
                  <MessageSquare size={36} style={{ opacity: 0.3 }} />
                  <p style={{ fontSize: '0.9rem' }}>Send a message to start the conversation.</p>
                </div>
              ) : (
                messages.map((msg) => (
                  <div 
                    key={msg.id} 
                    className={`chat-bubble ${msg.is_mine ? 'sent' : 'received'}`}
                  >
                    {!msg.is_mine && (
                      <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--accent-purple)', marginBottom: '4px' }}>
                        {msg.sender_name}
                      </div>
                    )}
                    <div>{msg.content}</div>
                    <div className="chat-time">
                      {new Date(msg.created_at).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </div>
                  </div>
                ))
              )}
              <div ref={messagesEndRef} />
            </div>

            <form onSubmit={handleSendMessage} className="chat-input-area">
              <input 
                type="text" 
                className="form-input" 
                placeholder="Type your message..."
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                style={{ flex: 1 }}
              />
              <button type="submit" className="btn btn-primary" style={{ padding: '0 20px' }}>
                <Send size={16} />
              </button>
            </form>
          </div>
        ) : (
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', color: 'var(--text-secondary)', gap: '12px' }}>
            <MessageSquare size={40} style={{ opacity: 0.4 }} />
            <h3>Select a conversation</h3>
            <p style={{ fontSize: '0.9rem' }}>Pick a chat room from the sidebar listing to exchange messages.</p>
          </div>
        )}

        {/* AI Chat highlights Side panel */}
        {activeConvId && (
          <div className="chat-highlights-panel">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <span style={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem', color: 'var(--accent-purple)' }}>
                <Sparkles size={16} /> AI Chat Highlights
              </span>
              <button 
                onClick={fetchHighlights} 
                style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }}
                disabled={loadingHighlights}
                title="Refresh Highlights"
              >
                <RefreshCw size={14} style={{ animation: loadingHighlights ? 'spin 1.5s linear infinite' : 'none' }} />
              </button>
            </div>

            {highlights.length === 0 ? (
              <div style={{ display: 'flex', gap: '8px', padding: '10px', background: 'var(--bg-secondary)', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                <AlertCircle size={16} style={{ flexShrink: 0 }} /> No highlightable questions or action items detected in the last 20 messages.
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {highlights.map((hl, idx) => {
                  const isAction = hl.startsWith('Action:');
                  return (
                    <div 
                      key={idx} 
                      style={{ 
                        padding: '10px 12px', 
                        background: 'var(--bg-secondary)', 
                        border: '1px solid var(--border-color)', 
                        borderLeft: `3px solid ${isAction ? 'var(--accent-pink)' : 'var(--accent-purple)'}`,
                        borderRadius: 'var(--border-radius-sm)',
                        fontSize: '0.75rem', 
                        lineHeight: '1.4' 
                      }}
                    >
                      <div style={{ fontWeight: 600, color: isAction ? 'var(--accent-pink)' : 'var(--accent-purple)', marginBottom: '2px' }}>
                        {isAction ? 'Action Item' : 'Question Asked'}
                      </div>
                      <div style={{ color: 'var(--text-primary)' }}>
                        {hl.replace('Action:', '').replace('Question:', '').trim()}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}

      </div>
    </div>
  );
};

export default Chat;
