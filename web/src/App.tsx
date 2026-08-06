import { useState, useEffect } from 'react';
import { safeStorage } from './utils/safeStorage';
import { usePersistentState, isArray } from './utils/usePersistentState';
import { PhoneFrame } from './components/PhoneFrame';
import { DashboardView } from './components/DashboardView';
import { TransactionsView } from './components/TransactionsView';
import { AnalyticsView } from './components/AnalyticsView';
import { BudgetView } from './components/BudgetView';
import { CategoriesView } from './components/CategoriesView';
import { SubscriptionsView } from './components/SubscriptionsView';
import { SavingsView } from './components/SavingsView';
import { BulkImportView } from './components/BulkImportView';
import { AddExpenseModal } from './components/AddExpenseModal';
import { BulkImportModal } from './components/BulkImportModal';
import { SettingsModal } from './components/SettingsModal';
import { LockScreen } from './components/LockScreen';
import { INITIAL_CATEGORIES, INITIAL_PAYMENT_METHODS, INITIAL_EXPENSES, INITIAL_BUDGETS, INITIAL_SUBSCRIPTIONS, INITIAL_SAVINGS_GOALS } from './data/mockData';
import type { Expense, Category, PaymentMethod, Budget, Subscription, SavingGoal, AppSettings, TransactionType } from './types';

export const APP_VERSION = 1;
const VERSION_KEY = 'et_app_version';

const DEFAULT_SETTINGS: AppSettings = {
  currency: '₹',
  darkMode: true,
  isPinLocked: false,
  pin: '',
  viewMode: 'PHONE_FRAME'
};

export function App() {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'history' | 'analytics' | 'budget' | 'categories' | 'subscriptions' | 'savings' | 'bulk_import'>('dashboard');

  useEffect(() => {
    const savedVersion = safeStorage.getItem(VERSION_KEY);
    if (!savedVersion) {
      safeStorage.setItem(VERSION_KEY, String(APP_VERSION));
    }
  }, []);

  const [expenses, setExpenses] = usePersistentState<Expense[]>(
    'et_expenses',
    INITIAL_EXPENSES,
    (value) => (isArray<Expense>(value) ? value : INITIAL_EXPENSES)
  );

  const [categories, setCategories] = usePersistentState<Category[]>(
    'et_categories',
    INITIAL_CATEGORIES,
    (value) => (isArray<Category>(value) ? value : INITIAL_CATEGORIES)
  );

  const [paymentMethods, setPaymentMethods] = usePersistentState<PaymentMethod[]>(
    'et_payment_methods',
    INITIAL_PAYMENT_METHODS,
    (value) => {
      if (!isArray<PaymentMethod>(value)) return INITIAL_PAYMENT_METHODS;
      const filtered = value.filter(pm => pm.type === 'UPI' || pm.type === 'CASH');
      return filtered.length > 0 ? filtered : INITIAL_PAYMENT_METHODS;
    }
  );

  const [budgets, setBudgets] = usePersistentState<Budget[]>(
    'et_budgets',
    INITIAL_BUDGETS,
    (value) => (isArray<Budget>(value) ? value : INITIAL_BUDGETS)
  );

  const [subscriptions, setSubscriptions] = usePersistentState<Subscription[]>(
    'et_subs',
    INITIAL_SUBSCRIPTIONS,
    (value) => (isArray<Subscription>(value) ? value : INITIAL_SUBSCRIPTIONS)
  );

  const [savingsGoals] = useState<SavingGoal[]>(INITIAL_SAVINGS_GOALS);

  const [settings, setSettings] = usePersistentState<AppSettings>(
    'et_settings',
    DEFAULT_SETTINGS,
    (value) => {
      if (typeof value !== 'object' || value === null) return DEFAULT_SETTINGS;
      return { ...DEFAULT_SETTINGS, ...(value as Partial<AppSettings>) };
    }
  );

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingExpense, setEditingExpense] = useState<Expense | null>(null);
  const [isBulkImportOpen, setIsBulkImportOpen] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [isUnlocked, setIsUnlocked] = useState(!settings.isPinLocked);

  const updateSettings = (patch: Partial<AppSettings>) => {
    setSettings(prev => ({ ...prev, ...patch }));
  };

  const handleImport = (data: {
    expenses: Expense[];
    categories: Category[];
    paymentMethods: PaymentMethod[];
    budgets: Budget[];
    subscriptions: Subscription[];
    settings: AppSettings;
  }) => {
    setExpenses(data.expenses);
    setCategories(data.categories);
    setPaymentMethods(data.paymentMethods);
    setBudgets(data.budgets);
    setSubscriptions(data.subscriptions);
    setSettings(data.settings);
  };

  useEffect(() => {
    setBudgets(prev =>
      prev.map(bgt => {
        const spent = expenses
          .filter(e => e.type === 'EXPENSE' && e.categoryId === bgt.categoryId && e.date.slice(0, 7) === bgt.monthYear)
          .reduce((sum, e) => sum + e.amount, 0);
        return spent === bgt.spentAmount ? bgt : { ...bgt, spentAmount: spent };
      })
    );
  }, [expenses, setBudgets]);

  const handleAddTransaction = (newTx: Omit<Expense, 'id'>) => {
    const created: Expense = {
      ...newTx,
      id: `exp_${Date.now()}`
    };

    setExpenses(prev => [created, ...prev]);
  };

  const handleImportBulk = (rawTransactions: Array<{
    date: string;
    title: string;
    amount: number;
    type: TransactionType;
    categoryName: string;
    paymentMethodName: string;
    notes?: string;
  }>) => {
    const createdList: Expense[] = rawTransactions.map((t, idx) => {
      const matchCat = categories.find(c => c.name.toLowerCase() === t.categoryName.toLowerCase()) || categories[0];
      const matchPM = paymentMethods.find(pm => pm.name.toLowerCase().includes(t.paymentMethodName.toLowerCase())) || paymentMethods[0];

      return {
        id: `exp_${Date.now()}_${idx}`,
        title: t.title,
        amount: t.amount,
        type: t.type,
        categoryId: matchCat.id,
        categoryName: matchCat.name,
        categoryIcon: matchCat.icon,
        categoryColor: matchCat.color,
        paymentMethodId: matchPM.id,
        paymentMethodName: matchPM.name,
        date: t.date,
        notes: t.notes
      };
    });

    setExpenses(prev => [...createdList, ...prev]);
  };

  const handleDeleteExpense = (id: string) => {
    setExpenses(prev => prev.filter(e => e.id !== id));
  };

  const handleEditExpense = (id: string) => {
    const target = expenses.find(e => e.id === id);
    if (target) {
      setEditingExpense(target);
      setIsAddModalOpen(true);
    }
  };

  const handleUpdateTransaction = (id: string, newTx: Omit<Expense, 'id'>) => {
    setExpenses(prev => prev.map(e => (e.id === id ? { ...newTx, id } : e)));
    setEditingExpense(null);
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

  const handleAddSubscription = (sub: Omit<Subscription, 'id'>) => {
    const created: Subscription = {
      ...sub,
      id: `sub_${Date.now()}`
    };
    setSubscriptions(prev => [...prev, created]);
  };

  const handleDeleteSubscription = (id: string) => {
    setSubscriptions(prev => prev.filter(s => s.id !== id));
  };

  const handleToggleSubscription = (id: string) => {
    setSubscriptions(prev => prev.map(s => s.id === id ? { ...s, active: !s.active } : s));
  };

  const handleResetAllData = () => {
    safeStorage.clear();
    safeStorage.setItem('et_expenses', JSON.stringify([]));
    safeStorage.setItem('et_categories', JSON.stringify(INITIAL_CATEGORIES));
    safeStorage.setItem('et_payment_methods', JSON.stringify(INITIAL_PAYMENT_METHODS));
    safeStorage.setItem('et_budgets', JSON.stringify(INITIAL_BUDGETS.map(b => ({ ...b, spentAmount: 0 }))));
    safeStorage.setItem('et_subs', JSON.stringify([]));
    
    setExpenses([]);
    setCategories(INITIAL_CATEGORIES);
    setPaymentMethods(INITIAL_PAYMENT_METHODS);
    setBudgets(INITIAL_BUDGETS.map(b => ({ ...b, spentAmount: 0 })));
    setSubscriptions([]);
    setActiveTab('dashboard');
    window.location.reload();
  };

  return (
    <div style={{ width: '100vw', minHeight: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <PhoneFrame
        activeTab={activeTab}
        onChangeTab={setActiveTab}
        onResetAllData={handleResetAllData}
        onOpenSettings={() => setIsSettingsOpen(true)}
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
            onEditExpense={handleEditExpense}
            onOpenBulkImport={() => setIsBulkImportOpen(true)}
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
            onAddSubscription={handleAddSubscription}
            onDeleteSubscription={handleDeleteSubscription}
          />
        )}

        {activeTab === 'savings' && (
          <SavingsView
            savingsGoals={savingsGoals}
            currency={settings.currency}
          />
        )}

        {activeTab === 'bulk_import' && (
          <BulkImportView
            categories={categories}
            paymentMethods={paymentMethods}
            currency={settings.currency}
            onConfirmImport={(rawTransactions) => {
              handleImportBulk(rawTransactions);
              setActiveTab('history');
            }}
          />
        )}
      </PhoneFrame>

      <AddExpenseModal
        isOpen={isAddModalOpen}
        onClose={() => {
          setIsAddModalOpen(false);
          setEditingExpense(null);
        }}
        categories={categories}
        paymentMethods={paymentMethods}
        currency={settings.currency}
        editing={editingExpense}
        onAddTransaction={handleAddTransaction}
        onUpdateTransaction={handleUpdateTransaction}
      />

      <BulkImportModal
        isOpen={isBulkImportOpen}
        onClose={() => setIsBulkImportOpen(false)}
        currency={settings.currency}
        onImportBulk={handleImportBulk}
      />

      <SettingsModal
        isOpen={isSettingsOpen}
        onClose={() => setIsSettingsOpen(false)}
        settings={settings}
        onUpdateSettings={updateSettings}
        backupData={{
          expenses,
          categories,
          paymentMethods,
          budgets,
          subscriptions,
          settings
        }}
        onImport={handleImport}
      />

      {settings.isPinLocked && !isUnlocked && (
        <LockScreen
          pin={settings.pin}
          onUnlock={() => setIsUnlocked(true)}
        />
      )}
    </div>
  );
}

export default App;
