import { useState, useEffect } from 'react';
import { PhoneFrame } from './components/PhoneFrame';
import { DashboardView } from './components/DashboardView';
import { TransactionsView } from './components/TransactionsView';
import { AnalyticsView } from './components/AnalyticsView';
import { BudgetView } from './components/BudgetView';
import { SettingsView } from './components/SettingsView';
import { MiniPlayerBar } from './components/MiniPlayerBar';
import { AddExpenseModal } from './components/AddExpenseModal';
import { INITIAL_CATEGORIES, INITIAL_PAYMENT_METHODS, INITIAL_EXPENSES, INITIAL_BUDGETS } from './data/mockData';
import type { Expense, Category, PaymentMethod, Budget, AppSettings } from './types';

export function App() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'history' | 'analytics' | 'budget' | 'settings'>('dashboard');
  const [viewMode, setViewMode] = useState<'PHONE_FRAME' | 'MINI_PLAYER' | 'FULL_SCREEN'>('PHONE_FRAME');

  const [expenses, setExpenses] = useState<Expense[]>(() => {
    const saved = localStorage.getItem('et_expenses');
    return saved ? JSON.parse(saved) : INITIAL_EXPENSES;
  });

  const [categories] = useState<Category[]>(INITIAL_CATEGORIES);
  const [paymentMethods] = useState<PaymentMethod[]>(INITIAL_PAYMENT_METHODS);

  const [budgets, setBudgets] = useState<Budget[]>(() => {
    const saved = localStorage.getItem('et_budgets');
    return saved ? JSON.parse(saved) : INITIAL_BUDGETS;
  });

  const [settings, setSettings] = useState<AppSettings>(() => {
    const saved = localStorage.getItem('et_settings');
    return saved ? JSON.parse(saved) : {
      currency: '$',
      darkMode: true,
      isPinLocked: false,
      pin: '1234',
      viewMode: 'PHONE_FRAME'
    };
  });

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  // Sync to local storage
  useEffect(() => {
    localStorage.setItem('et_expenses', JSON.stringify(expenses));
  }, [expenses]);

  useEffect(() => {
    localStorage.setItem('et_budgets', JSON.stringify(budgets));
  }, [budgets]);

  useEffect(() => {
    localStorage.setItem('et_settings', JSON.stringify(settings));
    document.documentElement.setAttribute('data-theme', settings.darkMode ? 'dark' : 'light');
  }, [settings]);

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

  const handleResetData = () => {
    setExpenses(INITIAL_EXPENSES);
    setBudgets(INITIAL_BUDGETS);
    localStorage.removeItem('et_expenses');
    localStorage.removeItem('et_budgets');
  };

  const totalIncome = expenses.filter(e => e.type === 'INCOME').reduce((acc, e) => acc + e.amount, 0);
  const totalExpense = expenses.filter(e => e.type === 'EXPENSE').reduce((acc, e) => acc + e.amount, 0);

  return (
    <div style={{ width: '100vw', minHeight: '100vh', padding: '30px 16px', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
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

        {activeTab === 'settings' && (
          <SettingsView
            settings={settings}
            onUpdateSettings={newSet => setSettings(prev => ({ ...prev, ...newSet }))}
            onResetData={handleResetData}
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
