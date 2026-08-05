import React from 'react';
import { Shield, Laptop, Sun, Award } from 'lucide-react';
import type { SavingGoal } from '../types';

interface SavingsViewProps {
  savingsGoals: SavingGoal[];
  currency: string;
}

const getGoalIcon = (iconName: string) => {
  switch (iconName) {
    case 'Shield': return <Shield size={18} />;
    case 'Laptop': return <Laptop size={18} />;
    case 'Sun': return <Sun size={18} />;
    default: return <Award size={18} />;
  }
};

export const SavingsView: React.FC<SavingsViewProps> = ({ savingsGoals, currency }) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <h3 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
          <Award size={20} style={{ color: '#10B981' }} /> Savings Targets
        </h3>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {savingsGoals.map(goal => {
          const percent = Math.min(100, (goal.currentAmount / goal.targetAmount) * 100);

          return (
            <div
              key={goal.id}
              style={{
                background: 'rgba(255, 255, 255, 0.04)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                borderRadius: 18,
                padding: 16
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <div style={{
                    width: 36,
                    height: 36,
                    borderRadius: 10,
                    background: `${goal.color}20`,
                    color: goal.color,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    {getGoalIcon(goal.icon)}
                  </div>
                  <div>
                    <div style={{ fontSize: 15, fontWeight: 700, color: '#FFF' }}>{goal.title}</div>
                    <div style={{ fontSize: 11, color: '#94A3B8' }}>Target: {goal.targetDate}</div>
                  </div>
                </div>

                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: 14, fontWeight: 700, color: goal.color }}>
                    {currency}{goal.currentAmount.toLocaleString()}
                  </div>
                  <div style={{ fontSize: 11, color: '#64748B' }}>
                    of {currency}{goal.targetAmount.toLocaleString()}
                  </div>
                </div>
              </div>

              <div style={{ height: 8, background: 'rgba(255, 255, 255, 0.08)', borderRadius: 99, overflow: 'hidden', marginBottom: 6 }}>
                <div style={{
                  width: `${percent}%`,
                  height: '100%',
                  background: goal.color,
                  borderRadius: 99,
                  transition: 'width 0.4s ease'
                }} />
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, color: '#94A3B8' }}>
                <span>{percent.toFixed(0)}% Saved</span>
                <span>{currency}{(goal.targetAmount - goal.currentAmount).toLocaleString()} remaining</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
