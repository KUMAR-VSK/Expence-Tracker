import React, { useState, useEffect } from 'react';
import { Home, List, PieChart, Target, Settings, Wifi, Battery, Signal, Smartphone, Minimize2 } from 'lucide-react';

interface PhoneFrameProps {
  activeTab: 'dashboard' | 'history' | 'analytics' | 'budget' | 'settings';
  onChangeTab: (tab: 'dashboard' | 'history' | 'analytics' | 'budget' | 'settings') => void;
  viewMode: 'PHONE_FRAME' | 'MINI_PLAYER' | 'FULL_SCREEN';
  onSwitchViewMode: (mode: 'PHONE_FRAME' | 'MINI_PLAYER' | 'FULL_SCREEN') => void;
  children: React.ReactNode;
}

export const PhoneFrame: React.FC<PhoneFrameProps> = ({
  activeTab,
  onChangeTab,
  viewMode,
  onSwitchViewMode,
  children
}) => {
  const [timeStr, setTimeStr] = useState('');

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setTimeStr(now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false }));
    };
    updateTime();
    const interval = setInterval(updateTime, 10000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 16 }}>
      <div style={{
        display: 'flex',
        alignItems: 'center',
        gap: 8,
        background: 'rgba(30, 41, 59, 0.8)',
        backdropFilter: 'blur(12px)',
        border: '1px solid rgba(255, 255, 255, 0.1)',
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

      <div className="phone-chassis">
        <div className="dynamic-island">
          <div className="camera-lens" />
          <div className="sensor-dot" />
        </div>

        <div className="phone-screen">
          <div className="phone-status-bar">
            <span>{timeStr || '17:04'}</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <Signal size={14} />
              <Wifi size={14} />
              <Battery size={16} />
            </div>
          </div>

          <div style={{
            flex: 1,
            overflowY: 'auto',
            padding: '12px 16px 80px 16px',
            display: 'flex',
            flexDirection: 'column'
          }}>
            {children}
          </div>

          <div style={{
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            height: 70,
            background: 'rgba(15, 23, 42, 0.94)',
            backdropFilter: 'blur(20px)',
            borderTop: '1px solid rgba(255, 255, 255, 0.08)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-around',
            paddingBottom: 12,
            zIndex: 90
          }}>
            {[
              { id: 'dashboard', label: 'Home', icon: Home },
              { id: 'history', label: 'History', icon: List },
              { id: 'analytics', label: 'Analytics', icon: PieChart },
              { id: 'budget', label: 'Budget', icon: Target },
              { id: 'settings', label: 'Settings', icon: Settings }
            ].map(item => {
              const Icon = item.icon;
              const isActive = activeTab === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => onChangeTab(item.id as any)}
                  style={{
                    background: 'none',
                    border: 'none',
                    color: isActive ? '#6366F1' : '#64748B',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    gap: 3,
                    cursor: 'pointer',
                    fontSize: 10,
                    fontWeight: 700,
                    transition: 'all 0.2s'
                  }}
                >
                  <Icon size={20} style={{ transform: isActive ? 'scale(1.1)' : 'scale(1)' }} />
                  {item.label}
                </button>
              );
            })}
          </div>

          <div className="home-indicator" />
        </div>
      </div>
    </div>
  );
};
