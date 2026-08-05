import React, { useState } from 'react';
import { X, Plus, Calendar } from 'lucide-react';
import type { Category, PaymentMethod, TransactionType } from '../types';

interface AddExpenseModalProps {
  isOpen: boolean;
  onClose: () => void;
  categories: Category[];
  paymentMethods: PaymentMethod[];
  currency: string;
  onAddTransaction: (newExpense: {
    title: string;
    amount: number;
    type: TransactionType;
    categoryId: string;
    categoryName: string;
    categoryIcon: string;
    categoryColor: string;
    paymentMethodId: string;
    paymentMethodName: string;
    date: string;
    notes?: string;
  }) => void;
}

export const AddExpenseModal: React.FC<AddExpenseModalProps> = ({
  isOpen,
  onClose,
  categories,
  paymentMethods,
  currency,
  onAddTransaction
}) => {
  if (!isOpen) return null;

  const [type, setType] = useState<TransactionType>('EXPENSE');
  const [title, setTitle] = useState('');
  const [amount, setAmount] = useState('');
  const [categoryId, setCategoryId] = useState(categories[0]?.id || '');
  const [paymentMethodId, setPaymentMethodId] = useState(paymentMethods[0]?.id || '');
  const [txDate, setTxDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [notes, setNotes] = useState('');

  const filteredCategories = categories.filter(c => c.type === type);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title || !amount || parseFloat(amount) <= 0 || !txDate) return;

    const selectedCategory = categories.find(c => c.id === categoryId) || categories[0];
    const selectedPM = paymentMethods.find(pm => pm.id === paymentMethodId) || paymentMethods[0];

    // Preserve selected back-date time
    const selectedDateTime = new Date(txDate);
    // Use current time offset if selected today, or 12:00 PM for past dates
    selectedDateTime.setHours(12, 0, 0, 0);

    onAddTransaction({
      title,
      amount: parseFloat(amount),
      type,
      categoryId: selectedCategory.id,
      categoryName: selectedCategory.name,
      categoryIcon: selectedCategory.icon,
      categoryColor: selectedCategory.color,
      paymentMethodId: selectedPM.id,
      paymentMethodName: selectedPM.name,
      date: selectedDateTime.toISOString(),
      notes: notes || undefined
    });

    setTitle('');
    setAmount('');
    setNotes('');
    setTxDate(new Date().toISOString().split('T')[0]);
    onClose();
  };

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
        background: '#1E293B',
        padding: 24,
        position: 'relative'
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

        <h3 style={{ fontSize: 20, fontWeight: 700, marginBottom: 20, display: 'flex', alignItems: 'center', gap: 8 }}>
          <Plus size={22} style={{ color: '#6366F1' }} /> Add Transaction
        </h3>

        <div style={{
          display: 'flex',
          background: 'rgba(255, 255, 255, 0.06)',
          borderRadius: 12,
          padding: 4,
          marginBottom: 20
        }}>
          <button
            type="button"
            onClick={() => { setType('EXPENSE'); setCategoryId(categories.find(c => c.type === 'EXPENSE')?.id || ''); }}
            style={{
              flex: 1,
              padding: '8px 0',
              borderRadius: 8,
              border: 'none',
              fontWeight: 600,
              cursor: 'pointer',
              background: type === 'EXPENSE' ? '#EF4444' : 'transparent',
              color: '#FFFFFF',
              transition: 'all 0.2s'
            }}
          >
            Expense
          </button>
          <button
            type="button"
            onClick={() => { setType('INCOME'); setCategoryId(categories.find(c => c.type === 'INCOME')?.id || ''); }}
            style={{
              flex: 1,
              padding: '8px 0',
              borderRadius: 8,
              border: 'none',
              fontWeight: 600,
              cursor: 'pointer',
              background: type === 'INCOME' ? '#10B981' : 'transparent',
              color: '#FFFFFF',
              transition: 'all 0.2s'
            }}
          >
            Income
          </button>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div>
            <label style={{ fontSize: 13, color: '#94A3B8', marginBottom: 6, display: 'block' }}>Amount ({currency})</label>
            <div style={{ position: 'relative' }}>
              <span style={{ position: 'absolute', left: 12, top: 12, color: '#64748B', fontWeight: 700 }}>{currency}</span>
              <input
                type="number"
                step="0.01"
                placeholder="0.00"
                value={amount}
                onChange={e => setAmount(e.target.value)}
                required
                style={{
                  width: '100%',
                  padding: '10px 12px 10px 36px',
                  borderRadius: 10,
                  background: 'rgba(255, 255, 255, 0.05)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  color: '#FFF',
                  fontSize: 18,
                  fontWeight: 700,
                  outline: 'none'
                }}
              />
            </div>
          </div>

          <div>
            <label style={{ fontSize: 13, color: '#94A3B8', marginBottom: 6, display: 'block' }}>Title / Merchant</label>
            <input
              type="text"
              placeholder="e.g. Grocery Store, Coffee"
              value={title}
              onChange={e => setTitle(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '10px 12px',
                borderRadius: 10,
                background: 'rgba(255, 255, 255, 0.05)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 14,
                outline: 'none'
              }}
            />
          </div>

          {/* Date Picker Field (Supports Back-Dated Transactions) */}
          <div>
            <label style={{ fontSize: 13, color: '#94A3B8', marginBottom: 6, display: 'flex', alignItems: 'center', gap: 6 }}>
              <Calendar size={14} style={{ color: '#6366F1' }} /> Transaction Date (Past / Custom Date)
            </label>
            <input
              type="date"
              value={txDate}
              onChange={e => setTxDate(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '10px 12px',
                borderRadius: 10,
                background: '#0F172A',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 14,
                outline: 'none'
              }}
            />
          </div>

          <div>
            <label style={{ fontSize: 13, color: '#94A3B8', marginBottom: 6, display: 'block' }}>Category</label>
            <select
              value={categoryId}
              onChange={e => setCategoryId(e.target.value)}
              style={{
                width: '100%',
                padding: '10px 12px',
                borderRadius: 10,
                background: '#0F172A',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 14,
                outline: 'none'
              }}
            >
              {filteredCategories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label style={{ fontSize: 13, color: '#94A3B8', marginBottom: 6, display: 'block' }}>Payment Method</label>
            <select
              value={paymentMethodId}
              onChange={e => setPaymentMethodId(e.target.value)}
              style={{
                width: '100%',
                padding: '10px 12px',
                borderRadius: 10,
                background: '#0F172A',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 14,
                outline: 'none'
              }}
            >
              {paymentMethods.map(pm => (
                <option key={pm.id} value={pm.id}>{pm.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label style={{ fontSize: 13, color: '#94A3B8', marginBottom: 6, display: 'block' }}>Notes (Optional)</label>
            <input
              type="text"
              placeholder="Add additional details..."
              value={notes}
              onChange={e => setNotes(e.target.value)}
              style={{
                width: '100%',
                padding: '10px 12px',
                borderRadius: 10,
                background: 'rgba(255, 255, 255, 0.05)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 14,
                outline: 'none'
              }}
            />
          </div>

          <button type="submit" className="btn-primary" style={{ marginTop: 10, width: '100%' }}>
            Save Transaction
          </button>
        </form>
      </div>
    </div>
  );
};
