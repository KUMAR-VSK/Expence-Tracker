import React, { useState } from 'react';
import { Tag, CreditCard, Plus, Trash2, Smartphone, Banknote } from 'lucide-react';
import type { Category, PaymentMethod, TransactionType } from '../types';

interface CategoriesViewProps {
  categories: Category[];
  paymentMethods: PaymentMethod[];
  onAddCategory: (category: Omit<Category, 'id'>) => void;
  onDeleteCategory: (id: string) => void;
  onAddPaymentMethod: (pm: Omit<PaymentMethod, 'id'>) => void;
  onDeletePaymentMethod: (id: string) => void;
}

export const CategoriesView: React.FC<CategoriesViewProps> = ({
  categories,
  paymentMethods,
  onAddCategory,
  onDeleteCategory,
  onAddPaymentMethod,
  onDeletePaymentMethod
}) => {
  const [activeSubTab, setActiveSubTab] = useState<'categories' | 'payments'>('categories');

  // Category form state
  const [catName, setCatName] = useState('');
  const [catType, setCatType] = useState<TransactionType>('EXPENSE');
  const [catColor, setCatColor] = useState('#6366F1');

  // Payment method form state
  const [pmName, setPmName] = useState('');
  const [pmType, setPmType] = useState<'CARD' | 'UPI' | 'CASH' | 'BANK'>('CARD');
  const [pmAcc, setPmAcc] = useState('');

  const handleCreateCategory = (e: React.FormEvent) => {
    e.preventDefault();
    if (!catName) return;
    onAddCategory({
      name: catName,
      type: catType,
      color: catColor,
      icon: catType === 'EXPENSE' ? 'ShoppingBag' : 'Briefcase'
    });
    setCatName('');
  };

  const handleCreatePaymentMethod = (e: React.FormEvent) => {
    e.preventDefault();
    if (!pmName) return;
    onAddPaymentMethod({
      name: pmName,
      type: pmType,
      icon: pmType === 'CARD' ? 'CreditCard' : pmType === 'UPI' ? 'Smartphone' : pmType === 'BANK' ? 'Building' : 'Banknote',
      accountNumber: pmAcc || 'Active'
    });
    setPmName('');
    setPmAcc('');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      {/* Subtab navigation */}
      <div style={{
        display: 'flex',
        background: 'rgba(255, 255, 255, 0.05)',
        borderRadius: 12,
        padding: 4
      }}>
        <button
          onClick={() => setActiveSubTab('categories')}
          style={{
            flex: 1,
            padding: '8px 0',
            borderRadius: 8,
            border: 'none',
            fontSize: 13,
            fontWeight: 700,
            cursor: 'pointer',
            background: activeSubTab === 'categories' ? '#6366F1' : 'transparent',
            color: '#FFF',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 6
          }}
        >
          <Tag size={14} /> Categories ({categories.length})
        </button>

        <button
          onClick={() => setActiveSubTab('payments')}
          style={{
            flex: 1,
            padding: '8px 0',
            borderRadius: 8,
            border: 'none',
            fontSize: 13,
            fontWeight: 700,
            cursor: 'pointer',
            background: activeSubTab === 'payments' ? '#6366F1' : 'transparent',
            color: '#FFF',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 6
          }}
        >
          <CreditCard size={14} /> Payments ({paymentMethods.length})
        </button>
      </div>

      {activeSubTab === 'categories' ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {/* Add Category Form */}
          <form onSubmit={handleCreateCategory} className="glass-card" style={{ padding: 14, display: 'flex', flexDirection: 'column', gap: 10 }}>
            <div style={{ fontSize: 13, fontWeight: 700, color: '#FFF' }}>Add New Category</div>
            <div style={{ display: 'flex', gap: 8 }}>
              <input
                type="text"
                placeholder="Category Name"
                value={catName}
                onChange={e => setCatName(e.target.value)}
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
              <select
                value={catType}
                onChange={e => setCatType(e.target.value as TransactionType)}
                style={{
                  background: '#0F172A',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  borderRadius: 10,
                  padding: '0 10px',
                  color: '#FFF',
                  fontSize: 12
                }}
              >
                <option value="EXPENSE">Expense</option>
                <option value="INCOME">Income</option>
              </select>
              <input
                type="color"
                value={catColor}
                onChange={e => setCatColor(e.target.value)}
                style={{ width: 36, height: 36, border: 'none', background: 'none', cursor: 'pointer' }}
              />
            </div>
            <button type="submit" className="btn-primary" style={{ padding: '8px 12px', fontSize: 13, width: '100%' }}>
              <Plus size={14} /> Add Category
            </button>
          </form>

          {/* Category List */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {categories.map(cat => {
              const canDelete = categories.filter(category => category.type === cat.type).length > 1;

              return (
              <div
                key={cat.id}
                style={{
                  background: 'rgba(255, 255, 255, 0.03)',
                  border: '1px solid rgba(255, 255, 255, 0.06)',
                  borderRadius: 14,
                  padding: '10px 12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <span style={{ width: 12, height: 12, borderRadius: '50%', background: cat.color }} />
                  <div>
                    <div style={{ fontSize: 13, fontWeight: 700, color: '#FFF' }}>{cat.name}</div>
                    <div style={{ fontSize: 10, color: '#94A3B8' }}>{cat.type}</div>
                  </div>
                </div>

                <button
                  onClick={() => onDeleteCategory(cat.id)}
                  disabled={!canDelete}
                  style={{ background: 'none', border: 'none', color: '#EF4444', cursor: canDelete ? 'pointer' : 'not-allowed', padding: 4, opacity: canDelete ? 1 : 0.35 }}
                  title={canDelete ? 'Remove Category' : `Keep at least one ${cat.type.toLowerCase()} category`}
                >
                  <Trash2 size={15} opacity={0.7} />
                </button>
              </div>
              );
            })}
          </div>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {/* Add Payment Method Form */}
          <form onSubmit={handleCreatePaymentMethod} className="glass-card" style={{ padding: 14, display: 'flex', flexDirection: 'column', gap: 10 }}>
            <div style={{ fontSize: 13, fontWeight: 700, color: '#FFF' }}>Add New Payment Method</div>
            <div style={{ display: 'flex', gap: 8 }}>
              <input
                type="text"
                placeholder="Method Name (e.g. Axis Bank)"
                value={pmName}
                onChange={e => setPmName(e.target.value)}
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
              <select
                value={pmType}
                onChange={e => setPmType(e.target.value as any)}
                style={{
                  background: '#0F172A',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  borderRadius: 10,
                  padding: '0 8px',
                  color: '#FFF',
                  fontSize: 12
                }}
              >
                <option value="UPI">Google Pay</option>
                <option value="CASH">Cash</option>
              </select>
            </div>
            <button type="submit" className="btn-primary" style={{ padding: '8px 12px', fontSize: 13, width: '100%' }}>
              <Plus size={14} /> Add Payment Method
            </button>
          </form>

          {/* Payment Method List */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {paymentMethods.map(pm => {
              const canDelete = paymentMethods.length > 1;

              return (
              <div
                key={pm.id}
                style={{
                  background: 'rgba(255, 255, 255, 0.03)',
                  border: '1px solid rgba(255, 255, 255, 0.06)',
                  borderRadius: 14,
                  padding: '10px 12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <div style={{
                    width: 32,
                    height: 32,
                    borderRadius: 10,
                    background: 'rgba(99, 102, 241, 0.15)',
                    color: '#6366F1',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    {pm.type === 'CASH' ? <Banknote size={16} /> : <Smartphone size={16} />}
                  </div>
                  <div>
                    <div style={{ fontSize: 13, fontWeight: 700, color: '#FFF' }}>{pm.name}</div>
                    <div style={{ fontSize: 10, color: '#94A3B8' }}>{pm.type === 'CASH' ? 'Cash Payment' : 'Digital UPI'}</div>
                  </div>
                </div>

                <button
                  onClick={() => onDeletePaymentMethod(pm.id)}
                  disabled={!canDelete}
                  style={{ background: 'none', border: 'none', color: '#EF4444', cursor: canDelete ? 'pointer' : 'not-allowed', padding: 4, opacity: canDelete ? 1 : 0.35 }}
                  title={canDelete ? 'Remove Payment Method' : 'Keep at least one payment method'}
                >
                  <Trash2 size={15} opacity={0.7} />
                </button>
              </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};
