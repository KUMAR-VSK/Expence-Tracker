import React from 'react';
import { PieChart } from 'lucide-react';
import type { Expense } from '../types';

interface AnalyticsViewProps {
  expenses: Expense[];
  currency: string;
}

export const AnalyticsView: React.FC<AnalyticsViewProps> = ({ expenses, currency }) => {
  const expenseItems = expenses.filter(e => e.type === 'EXPENSE');
  const totalExpense = expenseItems.reduce((acc, e) => acc + e.amount, 0);

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
        <PieChart size={20} style={{ color: '#6366F1' }} /> Spending Breakdown
      </h3>

      <div className="glass-card" style={{ padding: 20, textAlign: 'center' }}>
        <div style={{ fontSize: 13, color: '#94A3B8', marginBottom: 4 }}>Total August Spending</div>
        <div style={{ fontSize: 28, fontWeight: 800, color: '#F8FAFC' }}>
          {currency}{totalExpense.toFixed(2)}
        </div>

        <div style={{ marginTop: 20, display: 'flex', flexDirection: 'column', gap: 12 }}>
          {categoryList.map(cat => {
            const percentage = totalExpense > 0 ? (cat.amount / totalExpense) * 100 : 0;
            return (
              <div key={cat.name} style={{ textAlign: 'left' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, fontWeight: 600, marginBottom: 4 }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                    <span style={{ width: 10, height: 10, borderRadius: '50%', background: cat.color }} />
                    {cat.name}
                  </span>
                  <span>{currency}{cat.amount.toFixed(2)} ({percentage.toFixed(1)}%)</span>
                </div>
                <div style={{ height: 8, background: 'rgba(255, 255, 255, 0.08)', borderRadius: 99, overflow: 'hidden' }}>
                  <div style={{ width: `${percentage}%`, height: '100%', background: cat.color, borderRadius: 99, transition: 'width 0.5s ease-in-out' }} />
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
