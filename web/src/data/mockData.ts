import type { Category, PaymentMethod, Expense, Budget, Subscription, SavingGoal } from '../types';

export const INITIAL_CATEGORIES: Category[] = [
  { id: 'cat_1', name: 'Food & Dining', icon: 'Utensils', color: '#6366F1', type: 'EXPENSE' },
  { id: 'cat_2', name: 'Shopping', icon: 'ShoppingBag', color: '#EC4899', type: 'EXPENSE' },
  { id: 'cat_3', name: 'Transport', icon: 'Car', color: '#3B82F6', type: 'EXPENSE' },
  { id: 'cat_4', name: 'Bills & Utilities', icon: 'Zap', color: '#F59E0B', type: 'EXPENSE' },
  { id: 'cat_5', name: 'Entertainment', icon: 'Film', color: '#8B5CF6', type: 'EXPENSE' },
  { id: 'cat_6', name: 'Health & Fitness', icon: 'Activity', color: '#10B981', type: 'EXPENSE' },
  { id: 'cat_7', name: 'Salary', icon: 'Briefcase', color: '#10B981', type: 'INCOME' },
  { id: 'cat_8', name: 'Freelance', icon: 'Laptop', color: '#6366F1', type: 'INCOME' },
  { id: 'cat_9', name: 'Investments', icon: 'TrendingUp', color: '#06B6D4', type: 'INCOME' },
];

export const INITIAL_PAYMENT_METHODS: PaymentMethod[] = [
  { id: 'pm_1', name: 'HDFC Credit Card', type: 'CARD', icon: 'CreditCard', accountNumber: '•••• 4821' },
  { id: 'pm_2', name: 'Google Pay UPI', type: 'UPI', icon: 'Smartphone', accountNumber: 'user@okaxis' },
  { id: 'pm_3', name: 'Cash', type: 'CASH', icon: 'Banknote', accountNumber: 'Wallet' },
  { id: 'pm_4', name: 'ICICI Bank', type: 'BANK', icon: 'Building', accountNumber: '•••• 9102' },
];

export const INITIAL_EXPENSES: Expense[] = [
  {
    id: 'exp_1',
    title: 'Monthly Salary Credit',
    amount: 85000,
    type: 'INCOME',
    categoryId: 'cat_7',
    categoryName: 'Salary',
    categoryIcon: 'Briefcase',
    categoryColor: '#10B981',
    paymentMethodId: 'pm_4',
    paymentMethodName: 'ICICI Bank',
    date: new Date(Date.now() - 86400000 * 2).toISOString(),
    notes: 'August Tech Corp Salary'
  },
  {
    id: 'exp_2',
    title: 'Supermarket Grocery',
    amount: 3450,
    type: 'EXPENSE',
    categoryId: 'cat_1',
    categoryName: 'Food & Dining',
    categoryIcon: 'Utensils',
    categoryColor: '#6366F1',
    paymentMethodId: 'pm_1',
    paymentMethodName: 'HDFC Credit Card',
    date: new Date(Date.now() - 86400000 * 1).toISOString(),
    notes: 'Weekly fresh groceries'
  },
  {
    id: 'exp_3',
    title: 'Uber Commute',
    amount: 480,
    type: 'EXPENSE',
    categoryId: 'cat_3',
    categoryName: 'Transport',
    categoryIcon: 'Car',
    categoryColor: '#3B82F6',
    paymentMethodId: 'pm_2',
    paymentMethodName: 'Google Pay UPI',
    date: new Date(Date.now() - 3600000 * 5).toISOString(),
    notes: 'Office ride'
  },
  {
    id: 'exp_4',
    title: 'Electricity & Internet Bill',
    amount: 2200,
    type: 'EXPENSE',
    categoryId: 'cat_4',
    categoryName: 'Bills & Utilities',
    categoryIcon: 'Zap',
    categoryColor: '#F59E0B',
    paymentMethodId: 'pm_2',
    paymentMethodName: 'Google Pay UPI',
    date: new Date(Date.now() - 86400000 * 3).toISOString(),
    notes: 'Fiber broadband & electricity',
    isRecurring: true,
    recurringFrequency: 'MONTHLY'
  },
  {
    id: 'exp_5',
    title: 'Apparel Store Shopping',
    amount: 2899,
    type: 'EXPENSE',
    categoryId: 'cat_2',
    categoryName: 'Shopping',
    categoryIcon: 'ShoppingBag',
    categoryColor: '#EC4899',
    paymentMethodId: 'pm_1',
    paymentMethodName: 'HDFC Credit Card',
    date: new Date(Date.now() - 86400000 * 4).toISOString(),
    notes: 'Work wear shirts'
  },
  {
    id: 'exp_6',
    title: 'UI Design Freelance',
    amount: 18000,
    type: 'INCOME',
    categoryId: 'cat_8',
    categoryName: 'Freelance',
    categoryIcon: 'Laptop',
    categoryColor: '#6366F1',
    paymentMethodId: 'pm_4',
    paymentMethodName: 'ICICI Bank',
    date: new Date(Date.now() - 86400000 * 5).toISOString(),
    notes: 'Client milestone 2'
  }
];

export const INITIAL_BUDGETS: Budget[] = [
  {
    id: 'bgt_1',
    categoryId: 'cat_1',
    categoryName: 'Food & Dining',
    categoryIcon: 'Utensils',
    categoryColor: '#6366F1',
    limitAmount: 15000,
    spentAmount: 3450,
    monthYear: '2026-08'
  },
  {
    id: 'bgt_2',
    categoryId: 'cat_2',
    categoryName: 'Shopping',
    categoryIcon: 'ShoppingBag',
    categoryColor: '#EC4899',
    limitAmount: 10000,
    spentAmount: 2899,
    monthYear: '2026-08'
  },
  {
    id: 'bgt_3',
    categoryId: 'cat_3',
    categoryName: 'Transport',
    categoryIcon: 'Car',
    categoryColor: '#3B82F6',
    limitAmount: 5000,
    spentAmount: 480,
    monthYear: '2026-08'
  },
  {
    id: 'bgt_4',
    categoryId: 'cat_4',
    categoryName: 'Bills & Utilities',
    categoryIcon: 'Zap',
    categoryColor: '#F59E0B',
    limitAmount: 6000,
    spentAmount: 2200,
    monthYear: '2026-08'
  }
];

export const INITIAL_SUBSCRIPTIONS: Subscription[] = [
  { id: 'sub_1', name: 'Netflix 4K Ultra', amount: 649, categoryName: 'Entertainment', billingCycle: 'Monthly', dueDate: '12th', icon: 'Tv', active: true },
  { id: 'sub_2', name: 'Spotify Premium', amount: 119, categoryName: 'Entertainment', billingCycle: 'Monthly', dueDate: '18th', icon: 'Music', active: true },
  { id: 'sub_3', name: 'Google One 200GB', amount: 210, categoryName: 'Bills & Utilities', billingCycle: 'Monthly', dueDate: '25th', icon: 'Cloud', active: true },
  { id: 'sub_4', name: 'Cult.fit Gym Pass', amount: 1499, categoryName: 'Health & Fitness', billingCycle: 'Monthly', dueDate: '1st', icon: 'Activity', active: true }
];

export const INITIAL_SAVINGS_GOALS: SavingGoal[] = [
  { id: 'goal_1', title: 'Emergency Fund', targetAmount: 100000, currentAmount: 65000, targetDate: '2026-12', icon: 'Shield', color: '#10B981' },
  { id: 'goal_2', title: 'MacBook Pro M4', targetAmount: 220000, currentAmount: 140000, targetDate: '2026-11', icon: 'Laptop', color: '#6366F1' },
  { id: 'goal_3', title: 'Goa Vacation', targetAmount: 35000, currentAmount: 22500, targetDate: '2026-10', icon: 'Sun', color: '#F59E0B' }
];
