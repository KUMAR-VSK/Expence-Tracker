import React, { useState } from 'react';
import { Search, Trash2, Utensils, ShoppingBag, Car, Zap, Film, Activity, Briefcase, Laptop, Wallet, Volume2, FileSpreadsheet } from 'lucide-react';
import type { Expense } from '../types';

interface TransactionsViewProps {
  expenses: Expense[];
  currency: string;
  onDeleteExpense: (id: string) => void;
  onOpenBulkImport?: () => void;
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

export const TransactionsView: React.FC<TransactionsViewProps> = ({
  expenses,
  currency,
  onDeleteExpense,
  onOpenBulkImport
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState<'ALL' | 'EXPENSE' | 'INCOME'>('ALL');
  const [selectedMonth, setSelectedMonth] = useState<string>('ALL');

  // Compute available unique months from expenses
  const availableMonths = Array.from(new Set(expenses.map(e => {
    const d = new Date(e.date);
    return `${d.toLocaleString('default', { month: 'short' })} ${d.getFullYear()}`;
  })));

  const filtered = expenses.filter(e => {
    const d = new Date(e.date);
    const monthKey = `${d.toLocaleString('default', { month: 'short' })} ${d.getFullYear()}`;
    
    const matchesSearch = e.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          e.categoryName.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = filterType === 'ALL' || e.type === filterType;
    const matchesMonth = selectedMonth === 'ALL' || monthKey === selectedMonth;

    return matchesSearch && matchesType && matchesMonth;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <div style={{ display: 'flex', gap: 8 }}>
        <div style={{ position: 'relative', flex: 1 }}>
          <Search size={18} style={{ position: 'absolute', left: 14, top: 12, color: '#64748B' }} />
          <input
            type="text"
            placeholder="Search transactions..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            style={{
              width: '100%',
              padding: '10px 14px 10px 42px',
              borderRadius: 14,
              background: 'rgba(255, 255, 255, 0.05)',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              color: '#FFF',
              fontSize: 14,
              outline: 'none'
            }}
          />
        </div>

        {/* Month Selector Filter */}
        <select
          value={selectedMonth}
          onChange={e => setSelectedMonth(e.target.value)}
          style={{
            background: '#0F172A',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            borderRadius: 14,
            padding: '0 10px',
            color: '#FFF',
            fontSize: 12,
            fontWeight: 600,
            outline: 'none'
          }}
        >
          <option value="ALL">All Months</option>
          {availableMonths.map(m => (
            <option key={m} value={m}>{m}</option>
          ))}
        </select>

        {onOpenBulkImport && (
          <button
            onClick={onOpenBulkImport}
            style={{
              background: 'rgba(16, 185, 129, 0.15)',
              border: '1px solid rgba(16, 185, 129, 0.3)',
              borderRadius: 14,
              padding: '0 12px',
              color: '#34D399',
              fontSize: 12,
              fontWeight: 700,
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: 4
            }}
            title="Import Excel / CSV"
          >
            <FileSpreadsheet size={16} /> Import Excel
          </button>
        )}
      </div>

      {/* Type Filter Buttons */}
      <div style={{ display: 'flex', gap: 8 }}>
        {(['ALL', 'EXPENSE', 'INCOME'] as const).map(type => (
          <button
            key={type}
            onClick={() => setFilterType(type)}
            style={{
              flex: 1,
              padding: '6px 12px',
              borderRadius: 10,
              border: 'none',
              fontSize: 13,
              fontWeight: 600,
              cursor: 'pointer',
              background: filterType === type ? '#6366F1' : 'rgba(255, 255, 255, 0.06)',
              color: filterType === type ? '#FFF' : '#94A3B8',
              transition: 'all 0.2s'
            }}
          >
            {type === 'ALL' ? 'All' : type === 'EXPENSE' ? 'Expenses' : 'Income'}
          </button>
        ))}
      </div>

      {/* Transaction Item List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        {filtered.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 30, color: '#64748B', fontSize: 14 }}>
            No transactions found for selected filters.
          </div>
        ) : (
          filtered.map(exp => (
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
                  {exp.notes && (
                    <div style={{ fontSize: 11, color: '#64748B', marginTop: 2, fontStyle: 'italic' }}>
                      "{exp.notes}"
                    </div>
                  )}
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <div style={{ textAlign: 'right' }}>
                  <div style={{
                    fontSize: 15,
                    fontWeight: 700,
                    color: exp.type === 'INCOME' ? '#10B981' : '#EF4444'
                  }}>
                    {exp.type === 'INCOME' ? '+' : '-'}{currency}{exp.amount.toFixed(2)}
                  </div>
                  <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>
                    {new Date(exp.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                  </div>
                </div>

                <button
                  onClick={() => onDeleteExpense(exp.id)}
                  style={{
                    background: 'none',
                    border: 'none',
                    color: '#64748B',
                    cursor: 'pointer',
                    padding: 4
                  }}
                  title="Delete Transaction"
                >
                  <Trash2 size={16} color="#EF4444" opacity={0.7} />
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
