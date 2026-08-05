import { useState, useEffect } from 'react';
import { PhoneFrame } from './components/PhoneFrame';
import { DashboardView } from './components/DashboardView';
import { TransactionsView } from './components/TransactionsView';
import { AnalyticsView } from './components/AnalyticsView';
import { BudgetView } from './components/BudgetView';
import { CategoriesView } from './components/CategoriesView';
import { SubscriptionsView } from './components/SubscriptionsView';
import { SavingsView } from './components/SavingsView';
import { MiniPlayerBar } from './components/MiniPlayerBar';
import { AddExpenseModal } from './components/AddExpenseModal';
import { INITIAL_CATEGORIES, INITIAL_PAYMENT_METHODS, INITIAL_EXPENSES, INITIAL_BUDGETS, INITIAL_SUBSCRIPTIONS, INITIAL_SAVINGS_GOALS } from './data/mockData';
import type { Expense, Category, PaymentMethod, Budget, Subscription, SavingGoal, AppSettings } from './types';

// APP VERSION CONFIGURATION FOR PERSISTENT DATA MIGRATION
export const APP_VERSION = 1;
const VERSION_KEY = 'et_app_version';

export function App() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'history' | 'analytics' | 'budget' | 'categories' | 'subscriptions' | 'savings'>('dashboard');
  const [viewMode, setViewMode] = useState<'PHONE_FRAME' | 'MINI_PLAYER' | 'FULL_SCREEN'>('PHONE_FRAME');

  // Verify and record current version schema
  useEffect(() => {
    const savedVersion = localStorage.getItem(VERSION_KEY);
    if (!savedVersion) {
      localStorage.setItem(VERSION_KEY, String(APP_VERSION));
    }
  }, []);

  const [expenses, setExpenses] = useState<Expense[]>(() => {
    const saved = localStorage.getItem('et_expenses');
    if (saved) {
      try {
        const parsed: Expense[] = JSON.parse(saved);
        if (Array.isArray(parsed) && parsed.length > 0) {
          return parsed;
        }
      } catch (err) {
        console.error('Error loading saved expenses:', err);
      }
    }
    return INITIAL_EXPENSES;
  });

  const [categories, setCategories] = useState<Category[]>(() => {
    const saved = localStorage.getItem('et_categories');
    return saved ? JSON.parse(saved) : INITIAL_CATEGORIES;
  });

  const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>(() => {
    const saved = localStorage.getItem('et_payment_methods');
    if (saved) {
      const parsed: PaymentMethod[] = JSON.parse(saved);
      // Clean up legacy card/bank methods if upgrading from older drafts
      const filtered = parsed.filter(pm => pm.type === 'UPI' || pm.type === 'CASH');
      if (filtered.length > 0) return filtered;
    }
    return INITIAL_PAYMENT_METHODS;
  });

  const [budgets, setBudgets] = useState<Budget[]>(() => {
    const saved = localStorage.getItem('et_budgets');
    return saved ? JSON.parse(saved) : INITIAL_BUDGETS;
  });

  const [subscriptions, setSubscriptions] = useState<Subscription[]>(() => {
    const saved = localStorage.getItem('et_subs');
    return saved ? JSON.parse(saved) : INITIAL_SUBSCRIPTIONS;
  });

  const [savingsGoals] = useState<SavingGoal[]>(INITIAL_SAVINGS_GOALS);

  const [settings] = useState<AppSettings>({
    currency: '₹',
    darkMode: true,
    isPinLocked: false,
    pin: '1234',
    viewMode: 'PHONE_FRAME'
  });

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  // Persistent storage sync (Preserves data across updates)
  useEffect(() => {
    localStorage.setItem('et_expenses', JSON.stringify(expenses));
  }, [expenses]);

  useEffect(() => {
    localStorage.setItem('et_categories', JSON.stringify(categories));
  }, [categories]);

  useEffect(() => {
    localStorage.setItem('et_payment_methods', JSON.stringify(paymentMethods));
  }, [paymentMethods]);

  useEffect(() => {
    localStorage.setItem('et_budgets', JSON.stringify(budgets));
  }, [budgets]);

  useEffect(() => {
    localStorage.setItem('et_subs', JSON.stringify(subscriptions));
  }, [subscriptions]);

  const handleAddTransaction = (newTx: Omit<Expense, 'id'>) => {
    const created: Expense = {
      ...newTx,
      id: `exp_${Date.now()}`
    };

    setExpenses(prev => [created, ...prev]);

    if (created.type === 'EXPENSE') {
      setBudgets(prev => prev.map(bgt => {
        if (bgt.categoryId === created.categoryId) {
          return { ...bgt, spentAmount: bgt.spentAmount + created.amount };
        }
        return bgt;
      }));
    }
  };

  const handleDeleteExpense = (id: string) => {
    setExpenses(prev => prev.filter(e => e.id !== id));
  };

  const handleAddCategory = (cat: Omit<Category, 'id'>) => {
    const created: Category = {
      ...cat,
      id: `cat_${Date.now()}`
    };
    setCategories(prev => [...prev, created]);
  };

  const handleDeleteCategory = (id: string) => {
    setCategories(prev => prev.filter(c => c.id !== id));
  };

  const handleAddPaymentMethod = (pm: Omit<PaymentMethod, 'id'>) => {
    const created: PaymentMethod = {
      ...pm,
      id: `pm_${Date.now()}`
    };
    setPaymentMethods(prev => [...prev, created]);
  };

  const handleDeletePaymentMethod = (id: string) => {
    setPaymentMethods(prev => prev.filter(pm => pm.id !== id));
  };

  const handleToggleSubscription = (id: string) => {
    setSubscriptions(prev => prev.map(s => s.id === id ? { ...s, active: !s.active } : s));
  };

  const totalIncome = expenses.filter(e => e.type === 'INCOME').reduce((acc, e) => acc + e.amount, 0);
  const totalExpense = expenses.filter(e => e.type === 'EXPENSE').reduce((acc, e) => acc + e.amount, 0);

  return (
    <div style={{ width: '100vw', minHeight: '100vh', padding: '20px 16px', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <PhoneFrame
        activeTab={activeTab}
        onChangeTab={setActiveTab}
        viewMode={viewMode}
        onSwitchViewMode={setViewMode}
      >
        {activeTab === 'dashboard' && (
          <DashboardView
            expenses={expenses}
            currency={settings.currency}
            onOpenAddModal={() => setIsAddModalOpen(true)}
            onNavigateToHistory={() => setActiveTab('history')}
          />
        )}

        {activeTab === 'history' && (
          <TransactionsView
            expenses={expenses}
            currency={settings.currency}
            onDeleteExpense={handleDeleteExpense}
          />
        )}

        {activeTab === 'analytics' && (
          <AnalyticsView
            expenses={expenses}
            currency={settings.currency}
          />
        )}

        {activeTab === 'budget' && (
          <BudgetView
            budgets={budgets}
            currency={settings.currency}
          />
        )}

        {activeTab === 'categories' && (
          <CategoriesView
            categories={categories}
            paymentMethods={paymentMethods}
            onAddCategory={handleAddCategory}
            onDeleteCategory={handleDeleteCategory}
            onAddPaymentMethod={handleAddPaymentMethod}
            onDeletePaymentMethod={handleDeletePaymentMethod}
          />
        )}

        {activeTab === 'subscriptions' && (
          <SubscriptionsView
            subscriptions={subscriptions}
            currency={settings.currency}
            onToggleSubscription={handleToggleSubscription}
          />
        )}

        {activeTab === 'savings' && (
          <SavingsView
            savingsGoals={savingsGoals}
            currency={settings.currency}
          />
        )}
      </PhoneFrame>

      <MiniPlayerBar
        totalIncome={totalIncome}
        totalExpense={totalExpense}
        currency={settings.currency}
        recentExpense={expenses[0]}
        onOpenAddModal={() => setIsAddModalOpen(true)}
        onSwitchViewMode={setViewMode}
      />

      <AddExpenseModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        categories={categories}
        paymentMethods={paymentMethods}
        currency={settings.currency}
        onAddTransaction={handleAddTransaction}
      />
    </div>
  );
}

export default App;
