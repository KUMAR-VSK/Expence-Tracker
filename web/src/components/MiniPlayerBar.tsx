import React, { useState } from 'react';
import { Play, Pause, Plus, Maximize2, TrendingUp, TrendingDown, Volume2, Shield } from 'lucide-react';
import type { Expense } from '../types';

interface MiniPlayerBarProps {
  totalIncome: number;
  totalExpense: number;
  currency: string;
  recentExpense?: Expense;
  onOpenAddModal: () => void;
  onSwitchViewMode: (mode: 'PHONE_FRAME' | 'FULL_SCREEN') => void;
}

export const MiniPlayerBar: React.FC<MiniPlayerBarProps> = ({
  totalIncome,
  totalExpense,
  currency,
  recentExpense,
  onOpenAddModal,
  onSwitchViewMode
}) => {
  const [isPlayingAudio, setIsPlayingAudio] = useState(false);
  const netBalance = totalIncome - totalExpense;

  const toggleAudio = () => {
    setIsPlayingAudio(!isPlayingAudio);
    if (!isPlayingAudio) {
      setTimeout(() => setIsPlayingAudio(false), 4000);
    }
  };

  return (
    <div className="floating-miniplayer animate-fade-in">
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <div style={{
            width: 32,
            height: 32,
            borderRadius: '50%',
            background: 'linear-gradient(135deg, #6366F1, #A855F7)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 0 12px rgba(99, 102, 241, 0.5)'
          }}>
            <Shield size={16} color="#FFF" />
          </div>
          <div>
            <div style={{ fontSize: 11, color: '#94A3B8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5 }}>
              Mini Player • Expense Pulse
            </div>
            <div style={{ fontSize: 15, fontWeight: 800, color: '#FFF' }}>
              {currency}{netBalance.toLocaleString('en-US', { minimumFractionDigits: 2 })}
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          <button
            onClick={onOpenAddModal}
            className="btn-primary"
            style={{ padding: '6px 10px', fontSize: 12, borderRadius: 10 }}
            title="Quick Add Expense"
          >
            <Plus size={14} /> Add
          </button>

          <button
            onClick={() => onSwitchViewMode('PHONE_FRAME')}
            className="btn-secondary"
            style={{ padding: 6, borderRadius: 10, display: 'flex', alignItems: 'center' }}
            title="Expand to Mobile Phone Frame"
          >
            <Maximize2 size={16} />
          </button>
        </div>
      </div>

      <div style={{
        background: 'rgba(255, 255, 255, 0.05)',
        border: '1px solid rgba(255, 255, 255, 0.08)',
        borderRadius: 14,
        padding: '10px 12px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: 10
      }}>
        <button
          onClick={toggleAudio}
          style={{
            width: 34,
            height: 34,
            borderRadius: '50%',
            background: isPlayingAudio ? '#EF4444' : '#6366F1',
            border: 'none',
            color: '#FFF',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            flexShrink: 0,
            transition: 'all 0.2s'
          }}
          title="Play Voice Expense Summary"
        >
          {isPlayingAudio ? <Pause size={16} /> : <Play size={16} style={{ marginLeft: 2 }} />}
        </button>

        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: 12, fontWeight: 700, color: '#F8FAFC', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
            {recentExpense ? recentExpense.title : 'No recent transactions'}
          </div>
          <div style={{ fontSize: 11, color: isPlayingAudio ? '#818CF8' : '#94A3B8', display: 'flex', alignItems: 'center', gap: 4 }}>
            {isPlayingAudio ? (
              <>
                <Volume2 size={12} className="animate-pulse-glow" /> Playing audio note summary...
              </>
            ) : (
              <>
                {recentExpense?.type === 'INCOME' ? (
                  <span style={{ color: '#10B981', display: 'inline-flex', alignItems: 'center', gap: 2 }}>
                    <TrendingUp size={12} /> +{currency}{recentExpense.amount.toFixed(2)}
                  </span>
                ) : (
                  <span style={{ color: '#EF4444', display: 'inline-flex', alignItems: 'center', gap: 2 }}>
                    <TrendingDown size={12} /> -{currency}{recentExpense?.amount.toFixed(2)}
                  </span>
                )}
                • {recentExpense?.categoryName}
              </>
            )}
          </div>
        </div>

        <div style={{ textAlign: 'right', flexShrink: 0 }}>
          <div style={{ fontSize: 10, color: '#64748B' }}>Spent</div>
          <div style={{ fontSize: 12, fontWeight: 700, color: '#F87171' }}>
            {currency}{totalExpense.toFixed(0)}
          </div>
        </div>
      </div>
    </div>
  );
};
