import React, { useState } from 'react';
import { PieChart, Banknote, Smartphone, BarChart2, Calendar } from 'lucide-react';
import type { Expense } from '../types';

interface AnalyticsViewProps {
  expenses: Expense[];
  currency: string;
}

export const AnalyticsView: React.FC<AnalyticsViewProps> = ({ expenses, currency }) => {
  const [paymentFilter, setPaymentFilter] = useState<'ALL' | 'Cash' | 'Google Pay'>('ALL');
  const [selectedMonth, setSelectedMonth] = useState<string>('ALL');

  // Compute month-wise grouped data
  const monthlyGroups: Record<string, { income: number; expense: number; count: number }> = {};

  expenses.forEach(item => {
    const d = new Date(item.date);
    const monthKey = `${d.toLocaleString('default', { month: 'short' })} ${d.getFullYear()}`;
    if (!monthlyGroups[monthKey]) {
      monthlyGroups[monthKey] = { income: 0, expense: 0, count: 0 };
    }
    monthlyGroups[monthKey].count += 1;
    if (item.type === 'INCOME') {
      monthlyGroups[monthKey].income += item.amount;
    } else {
      monthlyGroups[monthKey].expense += item.amount;
    }
  });

  const availableMonths = Object.keys(monthlyGroups);

  // Filter expenses by payment method & month
  const filteredExpenses = expenses.filter(e => {
    const d = new Date(e.date);
    const monthKey = `${d.toLocaleString('default', { month: 'short' })} ${d.getFullYear()}`;
    
    const matchesMonth = selectedMonth === 'ALL' || monthKey === selectedMonth;
    const matchesPayment = paymentFilter === 'ALL' || e.paymentMethodName.toLowerCase().includes(paymentFilter.toLowerCase());
    
    return matchesMonth && matchesPayment;
  });

  const expenseItems = filteredExpenses.filter(e => e.type === 'EXPENSE');
  const totalExpense = expenseItems.reduce((acc, e) => acc + e.amount, 0);

  // Overall payment method totals for comparison chart
  const allExpenseItems = filteredExpenses.filter(e => e.type === 'EXPENSE');
  const overallTotal = allExpenseItems.reduce((acc, e) => acc + e.amount, 0);

  const paymentTotals: Record<string, number> = {
    'Google Pay': 0,
    'Cash': 0
  };

  allExpenseItems.forEach(exp => {
    if (exp.paymentMethodName.toLowerCase().includes('google pay') || exp.paymentMethodName.toLowerCase().includes('gpay')) {
      paymentTotals['Google Pay'] += exp.amount;
    } else {
      paymentTotals['Cash'] += exp.amount;
    }
  });

  // Group by category for active filter
  const categoryTotals: Record<string, { name: string; color: string; amount: number }> = {};

  expenseItems.forEach(exp => {
    if (!categoryTotals[exp.categoryName]) {
      categoryTotals[exp.categoryName] = { name: exp.categoryName, color: exp.categoryColor, amount: 0 };
    }
    categoryTotals[exp.categoryName].amount += exp.amount;
  });

  const categoryList = Object.values(categoryTotals).sort((a, b) => b.amount - a.amount);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <h3 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
        <PieChart size={20} style={{ color: '#6366F1' }} /> Expense Analytics & Charts
      </h3>

      {/* Month Selector Dropdown */}
      <div className="glass-card" style={{ padding: '12px 14px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 13, fontWeight: 700, color: '#FFF', display: 'flex', alignItems: 'center', gap: 6 }}>
          <Calendar size={15} style={{ color: '#6366F1' }} /> Month Filter:
        </div>
        <select
          value={selectedMonth}
          onChange={e => setSelectedMonth(e.target.value)}
          style={{
            background: '#0F172A',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            borderRadius: 10,
            padding: '6px 12px',
            color: '#FFF',
            fontSize: 13,
            fontWeight: 600,
            outline: 'none'
          }}
        >
          <option value="ALL">All Months</option>
          {availableMonths.map(m => (
            <option key={m} value={m}>{m}</option>
          ))}
        </select>
      </div>

      {/* Monthly-Wise Split Cards Summary */}
      <div className="glass-card" style={{ padding: 16 }}>
        <div style={{ fontSize: 13, fontWeight: 700, color: '#FFF', marginBottom: 12, display: 'flex', alignItems: 'center', gap: 6 }}>
          <BarChart2 size={16} style={{ color: '#F59E0B' }} /> Monthly Expense Split
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {availableMonths.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 10, color: '#64748B', fontSize: 12 }}>
              No monthly data available.
            </div>
          ) : (
            availableMonths.map(month => {
              const data = monthlyGroups[month];
              const maxExp = Math.max(...Object.values(monthlyGroups).map(g => g.expense), 1);
              const barWidth = Math.min((data.expense / maxExp) * 100, 100);

              return (
                <div
                  key={month}
                  onClick={() => setSelectedMonth(month === selectedMonth ? 'ALL' : month)}
                  style={{
                    background: selectedMonth === month ? 'rgba(99, 102, 241, 0.15)' : 'rgba(255, 255, 255, 0.03)',
                    border: selectedMonth === month ? '1px solid #6366F1' : '1px solid rgba(255, 255, 255, 0.06)',
                    borderRadius: 12,
                    padding: 10,
                    cursor: 'pointer',
                    transition: 'all 0.2s'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, fontWeight: 700, marginBottom: 4 }}>
                    <span style={{ color: '#FFF' }}>{month}</span>
                    <span style={{ color: '#EF4444' }}>{currency}{data.expense.toFixed(0)}</span>
                  </div>

                  <div style={{ height: 6, background: 'rgba(255, 255, 255, 0.08)', borderRadius: 99, overflow: 'hidden' }}>
                    <div style={{ width: `${barWidth}%`, height: '100%', background: '#EF4444', borderRadius: 99 }} />
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>

      {/* Payment Method Filter Toggle */}
      <div style={{
        display: 'flex',
        background: 'rgba(255, 255, 255, 0.05)',
        borderRadius: 14,
        padding: 4,
        gap: 4
      }}>
        <button
          onClick={() => setPaymentFilter('ALL')}
          style={{
            flex: 1,
            padding: '8px 0',
            borderRadius: 10,
            border: 'none',
            fontSize: 12,
            fontWeight: 700,
            cursor: 'pointer',
            background: paymentFilter === 'ALL' ? '#6366F1' : 'transparent',
            color: '#FFF',
            transition: 'all 0.2s'
          }}
        >
          All Payments
        </button>
        <button
          onClick={() => setPaymentFilter('Google Pay')}
          style={{
            flex: 1,
            padding: '8px 0',
            borderRadius: 10,
            border: 'none',
            fontSize: 12,
            fontWeight: 700,
            cursor: 'pointer',
            background: paymentFilter === 'Google Pay' ? '#6366F1' : 'transparent',
            color: '#FFF',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 4,
            transition: 'all 0.2s'
          }}
        >
          <Smartphone size={14} /> GPay
        </button>
        <button
          onClick={() => setPaymentFilter('Cash')}
          style={{
            flex: 1,
            padding: '8px 0',
            borderRadius: 10,
            border: 'none',
            fontSize: 12,
            fontWeight: 700,
            cursor: 'pointer',
            background: paymentFilter === 'Cash' ? '#6366F1' : 'transparent',
            color: '#FFF',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 4,
            transition: 'all 0.2s'
          }}
        >
          <Banknote size={14} /> Cash
        </button>
      </div>

      {/* Payment Method Percentage Share Widget */}
      <div className="glass-card" style={{ padding: 16 }}>
        <div style={{ fontSize: 13, fontWeight: 700, color: '#FFF', marginBottom: 12, display: 'flex', alignItems: 'center', gap: 6 }}>
          <BarChart2 size={16} style={{ color: '#10B981' }} /> Payment Mode Split
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 12 }}>
          <div style={{ background: 'rgba(99, 102, 241, 0.12)', border: '1px solid rgba(99, 102, 241, 0.2)', borderRadius: 14, padding: 12 }}>
            <div style={{ fontSize: 11, color: '#94A3B8', display: 'flex', alignItems: 'center', gap: 4 }}>
              <Smartphone size={13} style={{ color: '#6366F1' }} /> Google Pay
            </div>
            <div style={{ fontSize: 16, fontWeight: 800, color: '#FFF', marginTop: 4 }}>
              {currency}{paymentTotals['Google Pay'].toFixed(0)}
            </div>
            <div style={{ fontSize: 11, color: '#818CF8', fontWeight: 600 }}>
              {overallTotal > 0 ? ((paymentTotals['Google Pay'] / overallTotal) * 100).toFixed(1) : 0}% of total
            </div>
          </div>

          <div style={{ background: 'rgba(16, 185, 129, 0.12)', border: '1px solid rgba(16, 185, 129, 0.2)', borderRadius: 14, padding: 12 }}>
            <div style={{ fontSize: 11, color: '#94A3B8', display: 'flex', alignItems: 'center', gap: 4 }}>
              <Banknote size={13} style={{ color: '#10B981' }} /> Cash
            </div>
            <div style={{ fontSize: 16, fontWeight: 800, color: '#FFF', marginTop: 4 }}>
              {currency}{paymentTotals['Cash'].toFixed(0)}
            </div>
            <div style={{ fontSize: 11, color: '#34D399', fontWeight: 600 }}>
              {overallTotal > 0 ? ((paymentTotals['Cash'] / overallTotal) * 100).toFixed(1) : 0}% of total
            </div>
          </div>
        </div>

        {/* Visual Stacked Progress Bar */}
        <div style={{ height: 8, background: 'rgba(255, 255, 255, 0.08)', borderRadius: 99, display: 'flex', overflow: 'hidden' }}>
          <div style={{ width: `${overallTotal > 0 ? (paymentTotals['Google Pay'] / overallTotal) * 100 : 50}%`, background: '#6366F1' }} title="Google Pay Share" />
          <div style={{ width: `${overallTotal > 0 ? (paymentTotals['Cash'] / overallTotal) * 100 : 50}%`, background: '#10B981' }} title="Cash Share" />
        </div>
      </div>

      {/* Category Expense Percentages Breakdown Card */}
      <div className="glass-card" style={{ padding: 20 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <div>
            <div style={{ fontSize: 12, color: '#94A3B8' }}>{selectedMonth === 'ALL' ? 'Total Expenses' : selectedMonth}</div>
            <div style={{ fontSize: 24, fontWeight: 800, color: '#F8FAFC' }}>
              {currency}{totalExpense.toFixed(2)}
            </div>
          </div>
          <div style={{ fontSize: 11, background: 'rgba(255, 255, 255, 0.08)', padding: '4px 10px', borderRadius: 99, color: '#94A3B8', fontWeight: 600 }}>
            {categoryList.length} Categories
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {categoryList.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 20, color: '#64748B', fontSize: 13 }}>
              No expenses for selected filters.
            </div>
          ) : (
            categoryList.map(cat => {
              const percentage = totalExpense > 0 ? (cat.amount / totalExpense) * 100 : 0;
              return (
                <div key={cat.name} style={{ textAlign: 'left' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, fontWeight: 600, marginBottom: 6 }}>
                    <span style={{ display: 'flex', alignItems: 'center', gap: 6, color: '#FFF' }}>
                      <span style={{ width: 10, height: 10, borderRadius: '50%', background: cat.color }} />
                      {cat.name}
                    </span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <span style={{ fontSize: 12, background: `${cat.color}25`, color: cat.color, padding: '2px 8px', borderRadius: 99, fontWeight: 700 }}>
                        {percentage.toFixed(1)}%
                      </span>
                      <span style={{ color: '#FFF' }}>{currency}{cat.amount.toFixed(2)}</span>
                    </span>
                  </div>

                  <div style={{ height: 8, background: 'rgba(255, 255, 255, 0.08)', borderRadius: 99, overflow: 'hidden' }}>
                    <div style={{ width: `${percentage}%`, height: '100%', background: cat.color, borderRadius: 99, transition: 'width 0.5s ease-in-out' }} />
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
};
