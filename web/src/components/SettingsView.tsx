import React from 'react';
import { Settings as SettingsIcon, Lock, RefreshCw } from 'lucide-react';
import type { AppSettings } from '../types';

interface SettingsViewProps {
  settings: AppSettings;
  onUpdateSettings: (newSettings: Partial<AppSettings>) => void;
  onResetData: () => void;
}

export const SettingsView: React.FC<SettingsViewProps> = ({
  settings,
  onUpdateSettings,
  onResetData
}) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <h3 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
        <SettingsIcon size={20} style={{ color: '#6366F1' }} /> App Settings
      </h3>

      {/* Security PIN Lock Switch */}
      <div style={{
        background: 'rgba(255, 255, 255, 0.04)',
        border: '1px solid rgba(255, 255, 255, 0.08)',
        borderRadius: 18,
        padding: 16,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <Lock size={20} style={{ color: '#F59E0B' }} />
          <div>
            <div style={{ fontSize: 14, fontWeight: 700, color: '#FFF' }}>App PIN Security</div>
            <div style={{ fontSize: 12, color: '#94A3B8' }}>Require 4-digit PIN lock on startup</div>
          </div>
        </div>

        <input
          type="checkbox"
          checked={settings.isPinLocked}
          onChange={e => onUpdateSettings({ isPinLocked: e.target.checked })}
          style={{ width: 18, height: 18, accentColor: '#6366F1', cursor: 'pointer' }}
        />
      </div>

      {/* Reset Data */}
      <button
        onClick={onResetData}
        className="btn-secondary"
        style={{ width: '100%', padding: '12px', borderRadius: 14, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8, color: '#F87171' }}
      >
        <RefreshCw size={16} /> Reset Default Mock Data
      </button>
    </div>
  );
};
