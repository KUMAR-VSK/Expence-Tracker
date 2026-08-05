import React, { useState } from 'react';
import { Calendar, CheckCircle, Clock, RefreshCw, Plus, Trash2, X } from 'lucide-react';
import type { Subscription } from '../types';

interface SubscriptionsViewProps {
  subscriptions: Subscription[];
  currency: string;
  onToggleSubscription: (id: string) => void;
  onAddSubscription: (sub: Omit<Subscription, 'id'>) => void;
  onDeleteSubscription: (id: string) => void;
}

export const SubscriptionsView: React.FC<SubscriptionsViewProps> = ({
  subscriptions,
  currency,
  onToggleSubscription,
  onAddSubscription,
  onDeleteSubscription
}) => {
  const [isAddFormOpen, setIsAddFormOpen] = useState(false);
  const [name, setName] = useState('');
  const [amount, setAmount] = useState('');
  const [billingCycle, setBillingCycle] = useState<'Monthly' | 'Yearly'>('Monthly');
  const [dueDate, setDueDate] = useState('1st');

  const totalMonthlyCommitment = subscriptions
    .filter(s => s.active)
    .reduce((acc, s) => acc + (s.billingCycle === 'Monthly' ? s.amount : s.amount / 12), 0);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !amount || parseFloat(amount) <= 0) return;

    onAddSubscription({
      name,
      amount: parseFloat(amount),
      categoryName: 'Subscriptions',
      billingCycle,
      dueDate,
      icon: 'Tv',
      active: true
    });

    setName('');
    setAmount('');
    setDueDate('1st');
    setIsAddFormOpen(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <h3 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
          <RefreshCw size={20} style={{ color: '#6366F1' }} /> Recurring Subscriptions
        </h3>
        <button
          onClick={() => setIsAddFormOpen(!isAddFormOpen)}
          style={{
            background: isAddFormOpen ? 'rgba(239, 68, 68, 0.2)' : '#6366F1',
            border: 'none',
            borderRadius: 10,
            padding: '6px 12px',
            color: '#FFF',
            fontSize: 13,
            fontWeight: 600,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: 4
          }}
        >
          {isAddFormOpen ? <X size={16} /> : <Plus size={16} />}
          {isAddFormOpen ? 'Cancel' : 'Add New'}
        </button>
      </div>

      {/* Add Subscription Form */}
      {isAddFormOpen && (
        <form onSubmit={handleSubmit} className="glass-card animate-fade-in" style={{ padding: 16, display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div style={{ fontSize: 14, fontWeight: 700, color: '#FFF' }}>Add New Subscription</div>

          <div style={{ display: 'flex', gap: 8 }}>
            <input
              type="text"
              placeholder="Subscription Name (e.g. Netflix, Gym)"
              value={name}
              onChange={e => setName(e.target.value)}
              required
              style={{
                flex: 1,
                padding: '8px 12px',
                borderRadius: 10,
                background: 'rgba(255, 255, 255, 0.05)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 13,
                outline: 'none'
              }}
            />
            <input
              type="number"
              step="0.01"
              placeholder={`Amount (${currency})`}
              value={amount}
              onChange={e => setAmount(e.target.value)}
              required
              style={{
                width: 110,
                padding: '8px 12px',
                borderRadius: 10,
                background: 'rgba(255, 255, 255, 0.05)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 13,
                outline: 'none'
              }}
            />
          </div>

          <div style={{ display: 'flex', gap: 8 }}>
            <select
              value={billingCycle}
              onChange={e => setBillingCycle(e.target.value as any)}
              style={{
                flex: 1,
                padding: '8px 10px',
                borderRadius: 10,
                background: '#0F172A',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 13,
                outline: 'none'
              }}
            >
              <option value="Monthly">Monthly Cycle</option>
              <option value="Yearly">Yearly Cycle</option>
            </select>

            <input
              type="text"
              placeholder="Due Date (e.g. 5th)"
              value={dueDate}
              onChange={e => setDueDate(e.target.value)}
              required
              style={{
                flex: 1,
                padding: '8px 12px',
                borderRadius: 10,
                background: 'rgba(255, 255, 255, 0.05)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: '#FFF',
                fontSize: 13,
                outline: 'none'
              }}
            />
          </div>

          <button type="submit" className="btn-primary" style={{ padding: '8px 12px', fontSize: 13, width: '100%' }}>
            <Plus size={14} /> Save Subscription
          </button>
        </form>
      )}

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
        {subscriptions.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 20, color: '#64748B', fontSize: 13 }}>
            No recurring subscriptions added yet.
          </div>
        ) : (
          subscriptions.map(sub => (
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

              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
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

                <button
                  onClick={() => onDeleteSubscription(sub.id)}
                  style={{ background: 'none', border: 'none', color: '#EF4444', cursor: 'pointer', padding: 4 }}
                  title="Delete Subscription"
                >
                  <Trash2 size={16} opacity={0.7} />
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
