import React from 'react';
import { X, Calendar, CreditCard, Wallet, Tag, Utensils, ShoppingBag, Car, Zap, Film, Activity, Briefcase, Laptop, Volume2 } from 'lucide-react';
import type { Expense } from '../types';

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

interface ExpenseDetailModalProps {
  isOpen: boolean;
  onClose: () => void;
  expense: Expense | null;
  currency: string;
}

export const ExpenseDetailModal: React.FC<ExpenseDetailModalProps> = ({
  isOpen,
  onClose,
  expense,
  currency
}) => {
  if (!isOpen || !expense) return null;

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
        gap: 16,
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
          <Tag size={22} style={{ color: expense.type === 'INCOME' ? '#10B981' : '#EF4444' }} /> 
          {expense.type === 'INCOME' ? 'Income Detail' : 'Expense Detail'}
        </h3>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, paddingBottom: 12, borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
            <div style={{
              width: 48,
              height: 48,
              borderRadius: 14,
              background: `${expense.categoryColor}20`,
              color: expense.categoryColor,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              border: `1px solid ${expense.categoryColor}40`
            }}>
              {getCategoryIcon(expense.categoryIcon)}
            </div>
            <div>
              <div style={{ fontSize: 16, fontWeight: 700, color: '#FFF' }}>{expense.title}</div>
              <div style={{ fontSize: 11, color: '#94A3B8' }}>{expense.categoryName}</div>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            <div style={{ background: 'rgba(255,255,255,0.04)', borderRadius: 12, padding: '12px' }}>
              <div style={{ fontSize: 10, fontWeight: 700, color: '#94A3B8', textTransform: 'uppercase' }}>Amount</div>
              <div style={{ fontSize: 20, fontWeight: 800, color: expense.type === 'INCOME' ? '#10B981' : '#EF4444', marginTop: 4 }}>
                {expense.type === 'INCOME' ? '+' : '-'}{currency}{expense.amount.toFixed(2)}
              </div>
            </div>

            <div style={{ background: 'rgba(255,255,255,0.04)', borderRadius: 12, padding: '12px' }}>
              <div style={{ fontSize: 10, fontWeight: 700, color: '#94A3B8', textTransform: 'uppercase' }}>Date</div>
              <div style={{ fontSize: 13, fontWeight: 600, color: '#FFF', marginTop: 4 }}>
                {new Date(expense.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
              </div>
            </div>
          </div>

          <div style={{ background: 'rgba(255,255,255,0.04)', borderRadius: 12, padding: '12px' }}>
            <div style={{ fontSize: 10, fontWeight: 700, color: '#94A3B8', textTransform: 'uppercase' }}>Payment Method</div>
            <div style={{ fontSize: 13, fontWeight: 600, color: '#FFF', marginTop: 4, display: 'flex', alignItems: 'center', gap: 6 }}>
              {expense.paymentMethodId === 'pm_2' ? <Wallet size={14} /> : <CreditCard size={14} />}
              {expense.paymentMethodName}
            </div>
          </div>

          {expense.notes && (
            <div style={{ background: 'rgba(255,255,255,0.04)', borderRadius: 12, padding: '12px' }}>
              <div style={{ fontSize: 10, fontWeight: 700, color: '#94A3B8', textTransform: 'uppercase' }}>Notes</div>
              <div style={{ fontSize: 13, color: '#FFF', marginTop: 4, fontStyle: 'italic' }}>"{expense.notes}"</div>
            </div>
          )}

          {expense.hasAudioNote && (
            <div style={{ background: 'rgba(99, 102, 241, 0.1)', border: '1px solid rgba(99, 102, 241, 0.3)', borderRadius: 12, padding: '12px', display: 'flex', alignItems: 'center', gap: 8, color: '#818CF8' }}>
              <Volume2 size={16} /> Audio note attached
            </div>
          )}

          {expense.isRecurring && (
            <div style={{ background: 'rgba(245, 158, 11, 0.1)', border: '1px solid rgba(245, 158, 11, 0.3)', borderRadius: 12, padding: '12px', display: 'flex', alignItems: 'center', gap: 8, color: '#FBBF24' }}>
              <Calendar size={16} /> Recurring: {expense.recurringFrequency}
            </div>
          )}
        </div>

        <button onClick={onClose} className="btn-primary" style={{ marginTop: 8, width: '100%' }}>
          Close
        </button>
      </div>
    </div>
  );
};