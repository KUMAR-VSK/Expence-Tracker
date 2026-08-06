import React, { useState } from 'react';
import { X, Coins, Lock, Unlock } from 'lucide-react';
import type { AppSettings } from '../types';

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  settings: AppSettings;
  onUpdateSettings: (patch: Partial<AppSettings>) => void;
}

const CURRENCIES = ['₹', '$', '€', '£'] as const;

export const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  onClose,
  settings,
  onUpdateSettings
}) => {
  const [pinInput, setPinInput] = useState('');
  const [pinError, setPinError] = useState('');

  if (!isOpen) return null;

  const handleEnablePin = () => {
    const pin = pinInput.trim();
    if (!/^\d{4}$/.test(pin)) {
      setPinError('Enter a 4-digit PIN');
      return;
    }
    onUpdateSettings({ pin, isPinLocked: true });
    setPinInput('');
    setPinError('');
  };

  const handleDisablePin = () => {
    if (pinInput !== settings.pin) {
      setPinError('Incorrect PIN');
      return;
    }
    onUpdateSettings({ isPinLocked: false });
    setPinInput('');
    setPinError('');
  };

  return (
    <div style={{
      position: 'fixed',
      inset: 0,
      background: 'rgba(0, 0, 0, 0.75)',
      backdropFilter: 'blur(8px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000,
      padding: 16
    }}>
      <div className="glass-card animate-fade-in" style={{
        width: '100%',
        maxWidth: 420,
        maxHeight: '90vh',
        background: '#1E293B',
        padding: 24,
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        gap: 20,
        overflowY: 'auto'
      }}>
        <button onClick={onClose} style={{
          position: 'absolute',
          top: 16,
          right: 16,
          background: 'none',
          border: 'none',
          color: '#94A3B8',
          cursor: 'pointer'
        }}>
          <X size={20} />
        </button>

        <h3 style={{ fontSize: 18, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 8, color: '#FFF' }}>
          <Coins size={22} style={{ color: '#10B981' }} /> Settings
        </h3>

        {/* Currency */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <div style={{ fontSize: 13, fontWeight: 700, color: '#94A3B8' }}>Currency Symbol</div>
          <div style={{ display: 'flex', gap: 8 }}>
            {CURRENCIES.map(curr => (
              <button
                key={curr}
                onClick={() => onUpdateSettings({ currency: curr })}
                style={{
                  flex: 1,
                  padding: '10px 0',
                  borderRadius: 10,
                  cursor: 'pointer',
                  fontSize: 18,
                  fontWeight: 700,
                  background: settings.currency === curr ? 'rgba(99, 102, 241, 0.2)' : 'rgba(255, 255, 255, 0.06)',
                  border: settings.currency === curr ? '1px solid #6366F1' : '1px solid rgba(255, 255, 255, 0.1)',
                  color: settings.currency === curr ? '#818CF8' : '#F8FAFC'
                }}
              >
                {curr}
              </button>
            ))}
          </div>
        </div>

        {/* PIN Lock */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10, borderTop: '1px solid rgba(255, 255, 255, 0.08)', paddingTop: 16 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 13, fontWeight: 700, color: '#F8FAFC' }}>
              {settings.isPinLocked ? <Lock size={16} style={{ color: '#10B981' }} /> : <Unlock size={16} style={{ color: '#94A3B8' }} />}
              PIN Lock {settings.isPinLocked ? 'Enabled' : 'Disabled'}
            </div>
          </div>

          <div style={{ display: 'flex', gap: 8 }}>
            <input
              type="password"
              inputMode="numeric"
              maxLength={4}
              placeholder={settings.isPinLocked ? 'Enter current PIN to disable' : 'Set a 4-digit PIN'}
              value={pinInput}
              onChange={(e) => {
                setPinInput(e.target.value.replace(/\D/g, ''));
                setPinError('');
              }}
              style={{
                flex: 1,
                background: 'rgba(255, 255, 255, 0.06)',
                border: '1px solid rgba(255, 255, 255, 0.12)',
                borderRadius: 10,
                padding: '10px 12px',
                color: '#FFF',
                fontSize: 14,
                letterSpacing: 4,
                outline: 'none'
              }}
            />
            {settings.isPinLocked ? (
              <button
                onClick={handleDisablePin}
                className="btn-primary"
                style={{ padding: '10px 16px', fontSize: 13 }}
              >
                Disable
              </button>
            ) : (
              <button
                onClick={handleEnablePin}
                className="btn-primary"
                style={{ padding: '10px 16px', fontSize: 13 }}
              >
                Enable
              </button>
            )}
          </div>

          {pinError && <div style={{ fontSize: 12, color: '#EF4444' }}>{pinError}</div>}
        </div>
      </div>
    </div>
  );
};
