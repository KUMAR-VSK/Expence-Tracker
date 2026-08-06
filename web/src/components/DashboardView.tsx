import React from 'react';
import { Wallet, ArrowUpRight, ArrowDownRight, Plus, Utensils, ShoppingBag, Car, Zap, Film, Activity, Briefcase, Laptop, Volume2 } from 'lucide-react';
import type { Expense, Category } from '../types';

interface DashboardViewProps {
  expenses: Expense[];
  categories?: Category[];
  currency: string;
  userName: string;
  onOpenAddModal: () => void;
  onNavigateToHistory: () => void;
}

const getCategoryIcon = (iconName: string) => {
  switch (iconName) {
    case 'Utensils': return <Utensils size={18} />;
    case 'ShoppingBag': return <ShoppingBag size={18} />;
    case 'Car': return <Car size={18} />;
    case 'Zap': return <Zap size={18} />;
    case 'Film': return <Film size={18} />;
    case 'Activity': return <Activity size={18} />;
    case 'Briefcase': return <Briefcase size={18} />;
    case 'Laptop': return <Laptop size={18} />;
    default: return <Wallet size={18} />;
  }
};

export const DashboardView: React.FC<DashboardViewProps> = ({
  expenses,
  currency,
  userName,
  onOpenAddModal,
  onNavigateToHistory
}) => {
  const totalIncome = expenses.filter(e => e.type === 'INCOME').reduce((acc, e) => acc + e.amount, 0);
  const totalExpense = expenses.filter(e => e.type === 'EXPENSE').reduce((acc, e) => acc + e.amount, 0);
  const totalBalance = totalIncome - totalExpense;

  const now = new Date();
  const todayStr = now.toLocaleDateString('en-US', { weekday: 'long', day: '2-digit', month: 'long' });
  const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();

  const todaySpending = expenses
    .filter(e => e.type === 'EXPENSE' && new Date(e.date).getTime() >= todayStart)
    .reduce((acc, e) => acc + e.amount, 0);

  const highestExpense = expenses
    .filter(e => e.type === 'EXPENSE')
    .reduce((max, e) => e.amount > max ? e.amount : max, 0);

  const currentDay = now.getDate();
  const avgDaily = currentDay > 0 ? totalExpense / currentDay : 0;

  const recentExpenses = expenses.slice(0, 5);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      {/* Native APK Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ fontSize: 20, fontWeight: 800, color: 'var(--text-primary)' }}>
            {userName ? `Hello, ${userName} 👋` : 'Hello, Local User 👋'}
          </div>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
            {todayStr}
          </div>
        </div>
      </div>

      {/* Total Balance Card */}
      <div style={{
        background: 'linear-gradient(135deg, #4338CA 0%, #6366F1 50%, #8B5CF6 100%)',
        borderRadius: 24,
        padding: '22px 20px',
        color: '#FFFFFF',
        boxShadow: '0 12px 28px rgba(99, 102, 241, 0.35)',
        position: 'relative',
        overflow: 'hidden'
      }}>
        <div style={{ position: 'absolute', right: -20, top: -20, width: 120, height: 120, background: 'rgba(255, 255, 255, 0.1)', borderRadius: '50%' }} />

        <div style={{ fontSize: 13, opacity: 0.85, fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5 }}>
          Total Balance
        </div>
        <div style={{ fontSize: 32, fontWeight: 800, margin: '6px 0 16px 0', letterSpacing: '-0.5px' }}>
          {currency}{totalBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
          <div style={{ background: 'rgba(0, 0, 0, 0.2)', borderRadius: 14, padding: '10px 12px', display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{ width: 32, height: 32, borderRadius: '50%', background: 'rgba(16, 185, 129, 0.25)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <ArrowDownRight size={18} color="#34D399" />
            </div>
            <div>
              <div style={{ fontSize: 11, opacity: 0.8 }}>Income</div>
              <div style={{ fontSize: 14, fontWeight: 700, color: '#34D399' }}>+{currency}{totalIncome.toFixed(0)}</div>
            </div>
          </div>

          <div style={{ background: 'rgba(0, 0, 0, 0.2)', borderRadius: 14, padding: '10px 12px', display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{ width: 32, height: 32, borderRadius: '50%', background: 'rgba(239, 68, 68, 0.25)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <ArrowUpRight size={18} color="#F87171" />
            </div>
            <div>
              <div style={{ fontSize: 11, opacity: 0.8 }}>Expenses</div>
              <div style={{ fontSize: 14, fontWeight: 700, color: '#F87171' }}>-{currency}{totalExpense.toFixed(0)}</div>
            </div>
          </div>
        </div>
      </div>

      {/* 2x2 Stat Cards Grid (Matching Native APK) */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <div className="glass-card" style={{ padding: '12px 14px', borderRadius: 16 }}>
          <div style={{ fontSize: 10, fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Daily Average</div>
          <div style={{ fontSize: 16, fontWeight: 800, color: 'var(--text-primary)', marginTop: 4 }}>
            {currency}{avgDaily.toFixed(2)}
          </div>
        </div>

        <div className="glass-card" style={{ padding: '12px 14px', borderRadius: 16 }}>
          <div style={{ fontSize: 10, fontWeight: 700, color: '#EF4444', textTransform: 'uppercase' }}>Highest Expense</div>
          <div style={{ fontSize: 16, fontWeight: 800, color: '#EF4444', marginTop: 4 }}>
            {currency}{highestExpense.toFixed(2)}
          </div>
        </div>

        <div className="glass-card" style={{ padding: '12px 14px', borderRadius: 16 }}>
          <div style={{ fontSize: 10, fontWeight: 700, color: '#6366F1', textTransform: 'uppercase' }}>Today's Spending</div>
          <div style={{ fontSize: 16, fontWeight: 800, color: '#6366F1', marginTop: 4 }}>
            {currency}{todaySpending.toFixed(2)}
          </div>
        </div>

        <div className="glass-card" style={{ padding: '12px 14px', borderRadius: 16 }}>
          <div style={{ fontSize: 10, fontWeight: 700, color: '#10B981', textTransform: 'uppercase' }}>Count</div>
          <div style={{ fontSize: 16, fontWeight: 800, color: '#10B981', marginTop: 4 }}>
            {expenses.length}
          </div>
        </div>
      </div>

      <button
        onClick={onOpenAddModal}
        className="btn-primary"
        style={{ width: '100%', padding: '14px', borderRadius: 16, fontSize: 15 }}
      >
        <Plus size={18} /> Add New Expense / Income
      </button>

      <div>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
          <h4 style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)' }}>Recent Activity</h4>
          <button
            onClick={onNavigateToHistory}
            style={{ background: 'none', border: 'none', color: '#6366F1', fontSize: 13, fontWeight: 600, cursor: 'pointer' }}
          >
            See All
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {recentExpenses.map(exp => (
            <div
              key={exp.id}
              style={{
                background: 'rgba(255, 255, 255, 0.04)',
                border: '1px solid rgba(255, 255, 255, 0.06)',
                borderRadius: 16,
                padding: '12px 14px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <div style={{
                  width: 40,
                  height: 40,
                  borderRadius: 14,
                  background: `${exp.categoryColor}20`,
                  color: exp.categoryColor,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  border: `1px solid ${exp.categoryColor}40`
                }}>
                  {getCategoryIcon(exp.categoryIcon)}
                </div>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 6 }}>
                    {exp.title}
                    {exp.hasAudioNote && <Volume2 size={13} style={{ color: '#6366F1' }} />}
                  </div>
                  <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>
                    {exp.categoryName} • {exp.paymentMethodName}
                  </div>
                </div>
              </div>

              <div style={{ textAlign: 'right' }}>
                <div style={{
                  fontSize: 15,
                  fontWeight: 700,
                  color: exp.type === 'INCOME' ? '#10B981' : '#EF4444'
                }}>
                  {exp.type === 'INCOME' ? '+' : '-'}{currency}{exp.amount.toFixed(2)}
                </div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>
                  {new Date(exp.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
