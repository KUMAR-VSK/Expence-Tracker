import React from 'react';
import { Target, AlertTriangle, CheckCircle2 } from 'lucide-react';
import type { Budget } from '../types';

interface BudgetViewProps {
  budgets: Budget[];
  currency: string;
}

export const BudgetView: React.FC<BudgetViewProps> = ({ budgets, currency }) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <h3 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
        <Target size={20} style={{ color: '#6366F1' }} /> Category Budgets
      </h3>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {budgets.map(bgt => {
          const percent = Math.min(100, (bgt.spentAmount / bgt.limitAmount) * 100);
          const isOver = bgt.spentAmount > bgt.limitAmount;
          const isWarning = percent >= 80 && !isOver;

          return (
            <div
              key={bgt.id}
              style={{
                background: 'rgba(255, 255, 255, 0.04)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                borderRadius: 18,
                padding: 16
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <span style={{ width: 12, height: 12, borderRadius: '50%', background: bgt.categoryColor }} />
                  <span style={{ fontSize: 15, fontWeight: 700, color: '#FFF' }}>{bgt.categoryName}</span>
                </div>
                <div style={{ fontSize: 13, fontWeight: 700, color: isOver ? '#EF4444' : isWarning ? '#F59E0B' : '#10B981' }}>
                  {currency}{bgt.spentAmount.toFixed(0)} / {currency}{bgt.limitAmount.toFixed(0)}
                </div>
              </div>

              <div style={{ height: 10, background: 'rgba(255, 255, 255, 0.08)', borderRadius: 99, overflow: 'hidden', marginBottom: 8 }}>
                <div style={{
                  width: `${percent}%`,
                  height: '100%',
                  background: isOver ? '#EF4444' : isWarning ? '#F59E0B' : bgt.categoryColor,
                  borderRadius: 99,
                  transition: 'width 0.4s ease'
                }} />
              </div>

              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: 12, color: '#94A3B8' }}>
                <span>{percent.toFixed(0)}% spent</span>
                {isOver ? (
                  <span style={{ color: '#EF4444', display: 'flex', alignItems: 'center', gap: 4, fontWeight: 600 }}>
                    <AlertTriangle size={13} /> Over budget by {currency}{(bgt.spentAmount - bgt.limitAmount).toFixed(0)}
                  </span>
                ) : (
                  <span style={{ color: '#10B981', display: 'flex', alignItems: 'center', gap: 4 }}>
                    <CheckCircle2 size={13} /> {currency}{(bgt.limitAmount - bgt.spentAmount).toFixed(0)} remaining
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
