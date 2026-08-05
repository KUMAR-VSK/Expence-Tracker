import React, { useState, useEffect } from 'react';
import { Home, List, PieChart, Target, Wifi, Battery, Signal, Smartphone, Minimize2, RefreshCw, Award, Tag, Menu, X, Shield, RotateCcw } from 'lucide-react';

interface PhoneFrameProps {
  activeTab: 'dashboard' | 'history' | 'analytics' | 'budget' | 'categories' | 'subscriptions' | 'savings';
  onChangeTab: (tab: 'dashboard' | 'history' | 'analytics' | 'budget' | 'categories' | 'subscriptions' | 'savings') => void;
  viewMode: 'PHONE_FRAME' | 'MINI_PLAYER' | 'FULL_SCREEN';
  onSwitchViewMode: (mode: 'PHONE_FRAME' | 'MINI_PLAYER' | 'FULL_SCREEN') => void;
  onResetAllData: () => void;
  children: React.ReactNode;
}

export const PhoneFrame: React.FC<PhoneFrameProps> = ({
  activeTab,
  onChangeTab,
  viewMode,
  onSwitchViewMode,
  onResetAllData,
  children
}) => {
  const [timeStr, setTimeStr] = useState('');
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setTimeStr(now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false }));
    };
    updateTime();
    const interval = setInterval(updateTime, 10000);
    return () => clearInterval(interval);
  }, []);

  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: Home, desc: 'Overview & Recent Transactions' },
    { id: 'history', label: 'History & Search', icon: List, desc: 'View and filter all expenses' },
    { id: 'analytics', label: 'Analytics', icon: PieChart, desc: 'Category breakdown & stats' },
    { id: 'budget', label: 'Budget Planner', icon: Target, desc: 'Set & monitor monthly limits' },
    { id: 'categories', label: 'Manage Categories & Payments', icon: Tag, desc: 'Add / remove categories & cards' },
    { id: 'subscriptions', label: 'Recurring Subscriptions', icon: RefreshCw, desc: 'Track Netflix, Gym & utility bills' },
    { id: 'savings', label: 'Savings Goals', icon: Award, desc: 'Track financial targets' },
  ] as const;

  const handleConfirmReset = () => {
    if (window.confirm('Are you sure you want to reset all data? This will clear all transactions, categories, and settings.')) {
      onResetAllData();
      setIsDrawerOpen(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 16 }}>
      {/* Top Mode Switcher */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        gap: 8,
        background: 'rgba(15, 23, 42, 0.8)',
        backdropFilter: 'blur(12px)',
        border: '1px solid rgba(255, 255, 255, 0.08)',
        padding: '6px 12px',
        borderRadius: 99,
        boxShadow: '0 8px 20px rgba(0,0,0,0.3)'
      }}>
        <button
          onClick={() => onSwitchViewMode('PHONE_FRAME')}
          style={{
            background: viewMode === 'PHONE_FRAME' ? '#6366F1' : 'transparent',
            color: '#FFF',
            border: 'none',
            borderRadius: 99,
            padding: '6px 14px',
            fontSize: 12,
            fontWeight: 700,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: 6
          }}
        >
          <Smartphone size={14} /> Phone Mini Player
        </button>

        <button
          onClick={() => onSwitchViewMode('MINI_PLAYER')}
          style={{
            background: viewMode === 'MINI_PLAYER' ? '#6366F1' : 'transparent',
            color: '#FFF',
            border: 'none',
            borderRadius: 99,
            padding: '6px 14px',
            fontSize: 12,
            fontWeight: 700,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: 6
          }}
        >
          <Minimize2 size={14} /> Floating Widget
        </button>
      </div>

      {/* Phone Chassis */}
      <div className="phone-chassis">
        <div className="dynamic-island">
          <div className="camera-lens" />
          <div className="sensor-dot" />
        </div>

        <div className="phone-screen">
          {/* Status Bar */}
          <div className="phone-status-bar">
            <span>{timeStr || '17:40'}</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <Signal size={14} />
              <Wifi size={14} />
              <Battery size={16} />
            </div>
          </div>

          {/* Top Bar with 3-Bar Menu Button */}
          <div style={{
            padding: '8px 16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            borderBottom: '1px solid rgba(255, 255, 255, 0.06)'
          }}>
            <button
              onClick={() => setIsDrawerOpen(true)}
              style={{
                background: 'rgba(255, 255, 255, 0.06)',
                border: 'none',
                borderRadius: 10,
                padding: '6px 8px',
                color: '#FFF',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: 6
              }}
              title="Open Side Menu"
            >
              <Menu size={20} />
            </button>

            <div style={{ fontSize: 14, fontWeight: 800, color: '#FFF', textTransform: 'capitalize' }}>
              {activeTab === 'dashboard' ? 'Expense Tracker' : activeTab}
            </div>

            <div style={{ width: 34 }} />
          </div>

          {/* Side Drawer Overlay */}
          {isDrawerOpen && (
            <div style={{
              position: 'absolute',
              inset: 0,
              zIndex: 200,
              display: 'flex'
            }}>
              {/* Backdrop */}
              <div
                onClick={() => setIsDrawerOpen(false)}
                style={{
                  position: 'absolute',
                  inset: 0,
                  background: 'rgba(0, 0, 0, 0.65)',
                  backdropFilter: 'blur(4px)'
                }}
              />

              {/* Side Drawer Content */}
              <div style={{
                position: 'relative',
                width: '80%',
                height: '100%',
                background: '#0F172A',
                borderRight: '1px solid rgba(255, 255, 255, 0.1)',
                padding: 20,
                display: 'flex',
                flexDirection: 'column',
                boxShadow: '10px 0 30px rgba(0,0,0,0.8)',
                zIndex: 210
              }}>
                {/* Header */}
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div style={{ width: 32, height: 32, borderRadius: 10, background: '#6366F1', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#FFF' }}>
                      <Shield size={18} />
                    </div>
                    <div>
                      <div style={{ fontSize: 15, fontWeight: 800, color: '#FFF' }}>Expense Tracker</div>
                      <div style={{ fontSize: 11, color: '#94A3B8' }}>Menu & Features</div>
                    </div>
                  </div>
                  <button onClick={() => setIsDrawerOpen(false)} style={{ background: 'none', border: 'none', color: '#94A3B8', cursor: 'pointer' }}>
                    <X size={20} />
                  </button>
                </div>

                {/* Navigation Items */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: 6, flex: 1, overflowY: 'auto' }}>
                  {menuItems.map(item => {
                    const Icon = item.icon;
                    const isActive = activeTab === item.id;
                    return (
                      <button
                        key={item.id}
                        onClick={() => {
                          onChangeTab(item.id as any);
                          setIsDrawerOpen(false);
                        }}
                        style={{
                          background: isActive ? 'rgba(99, 102, 241, 0.15)' : 'transparent',
                          border: isActive ? '1px solid rgba(99, 102, 241, 0.3)' : '1px solid transparent',
                          borderRadius: 14,
                          padding: '10px 12px',
                          display: 'flex',
                          alignItems: 'center',
                          gap: 12,
                          color: isActive ? '#6366F1' : '#F8FAFC',
                          textAlign: 'left',
                          cursor: 'pointer',
                          transition: 'all 0.2s'
                        }}
                      >
                        <Icon size={18} color={isActive ? '#6366F1' : '#94A3B8'} />
                        <div>
                          <div style={{ fontSize: 13, fontWeight: 700 }}>{item.label}</div>
                          <div style={{ fontSize: 10, color: '#64748B' }}>{item.desc}</div>
                        </div>
                      </button>
                    );
                  })}
                </div>

                {/* Reset All Data Button */}
                <div style={{ paddingTop: 16, borderTop: '1px solid rgba(255, 255, 255, 0.08)' }}>
                  <button
                    onClick={handleConfirmReset}
                    style={{
                      width: '100%',
                      background: 'rgba(239, 68, 68, 0.12)',
                      border: '1px solid rgba(239, 68, 68, 0.3)',
                      borderRadius: 12,
                      padding: '10px 12px',
                      color: '#EF4444',
                      fontSize: 13,
                      fontWeight: 700,
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      gap: 8,
                      transition: 'all 0.2s'
                    }}
                  >
                    <RotateCcw size={16} /> Reset All App Data
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* Main Body Content */}
          <div style={{
            flex: 1,
            overflowY: 'auto',
            padding: '12px 16px 20px 16px',
            display: 'flex',
            flexDirection: 'column'
          }}>
            {children}
          </div>

          <div className="home-indicator" />
        </div>
      </div>
    </div>
  );
};
