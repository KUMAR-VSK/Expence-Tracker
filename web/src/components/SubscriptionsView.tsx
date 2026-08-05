import React from 'react';
import { Calendar, CheckCircle, Clock, RefreshCw } from 'lucide-react';
import type { Subscription } from '../types';

interface SubscriptionsViewProps {
  subscriptions: Subscription[];
  currency: string;
  onToggleSubscription: (id: string) => void;
}

export const SubscriptionsView: React.FC<SubscriptionsViewProps> = ({
  subscriptions,
  currency,
  onToggleSubscription
}) => {
  const totalMonthlyCommitment = subscriptions
    .filter(s => s.active)
    .reduce((acc, s) => acc + (s.billingCycle === 'Monthly' ? s.amount : s.amount / 12), 0);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <h3 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
        <RefreshCw size={20} style={{ color: '#6366F1' }} /> Recurring Subscriptions
      </h3>

      {/* Monthly Total Banner */}
      <div style={{
        background: 'rgba(99, 102, 241, 0.1)',
        border: '1px solid rgba(99, 102, 241, 0.2)',
        borderRadius: 16,
        padding: 16,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <div>
          <div style={{ fontSize: 12, color: '#94A3B8', fontWeight: 600 }}>Active Subscriptions</div>
          <div style={{ fontSize: 22, fontWeight: 800, color: '#FFF', marginTop: 2 }}>
            {currency}{totalMonthlyCommitment.toFixed(0)} <span style={{ fontSize: 13, color: '#94A3B8', fontWeight: 500 }}>/ month</span>
          </div>
        </div>
        <div style={{ fontSize: 12, fontWeight: 700, background: 'rgba(255, 255, 255, 0.08)', padding: '6px 12px', borderRadius: 99, color: '#6366F1' }}>
          {subscriptions.filter(s => s.active).length} Active
        </div>
      </div>

      {/* Subscription List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        {subscriptions.map(sub => (
          <div
            key={sub.id}
            style={{
              background: 'rgba(255, 255, 255, 0.03)',
              border: '1px solid rgba(255, 255, 255, 0.06)',
              borderRadius: 16,
              padding: '12px 14px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              opacity: sub.active ? 1 : 0.5
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div style={{
                width: 40,
                height: 40,
                borderRadius: 12,
                background: 'rgba(255, 255, 255, 0.06)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#6366F1'
              }}>
                <Calendar size={18} />
              </div>
              <div>
                <div style={{ fontSize: 14, fontWeight: 700, color: '#FFF' }}>{sub.name}</div>
                <div style={{ fontSize: 12, color: '#94A3B8', display: 'flex', alignItems: 'center', gap: 4 }}>
                  <Clock size={12} /> Due {sub.dueDate} • {sub.billingCycle}
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: 15, fontWeight: 700, color: '#F87171' }}>
                  {currency}{sub.amount}
                </div>
              </div>

              <button
                onClick={() => onToggleSubscription(sub.id)}
                style={{
                  background: sub.active ? 'rgba(16, 185, 129, 0.15)' : 'rgba(255, 255, 255, 0.06)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  borderRadius: 99,
                  padding: '4px 10px',
                  color: sub.active ? '#10B981' : '#94A3B8',
                  fontSize: 12,
                  fontWeight: 600,
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: 4
                }}
              >
                <CheckCircle size={12} /> {sub.active ? 'Active' : 'Paused'}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
