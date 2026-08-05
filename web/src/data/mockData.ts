import type { Category, PaymentMethod, Expense, Budget } from '../types';

export const INITIAL_CATEGORIES: Category[] = [
  { id: 'cat_1', name: 'Food & Dining', icon: 'Utensils', color: '#EF4444', type: 'EXPENSE' },
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
    amount: 4500,
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
    title: 'Whole Foods Grocery',
    amount: 142.50,
    type: 'EXPENSE',
    categoryId: 'cat_1',
    categoryName: 'Food & Dining',
    categoryIcon: 'Utensils',
    categoryColor: '#EF4444',
    paymentMethodId: 'pm_1',
    paymentMethodName: 'HDFC Credit Card',
    date: new Date(Date.now() - 86400000 * 1).toISOString(),
    notes: 'Weekly fresh groceries & organic fruit',
    hasAudioNote: true
  },
  {
    id: 'exp_3',
    title: 'Uber Ride to Downtown',
    amount: 24.80,
    type: 'EXPENSE',
    categoryId: 'cat_3',
    categoryName: 'Transport',
    categoryIcon: 'Car',
    categoryColor: '#3B82F6',
    paymentMethodId: 'pm_2',
    paymentMethodName: 'Google Pay UPI',
    date: new Date(Date.now() - 3600000 * 5).toISOString(),
    notes: 'Ride back from office client meeting'
  },
  {
    id: 'exp_4',
    title: 'Electricity & Wi-Fi Bill',
    amount: 88.00,
    type: 'EXPENSE',
    categoryId: 'cat_4',
    categoryName: 'Bills & Utilities',
    categoryIcon: 'Zap',
    categoryColor: '#F59E0B',
    paymentMethodId: 'pm_2',
    paymentMethodName: 'Google Pay UPI',
    date: new Date(Date.now() - 86400000 * 3).toISOString(),
    notes: 'Fiber Optic broadband & power bill'
  },
  {
    id: 'exp_5',
    title: 'Nike Air Max Sneakers',
    amount: 129.99,
    type: 'EXPENSE',
    categoryId: 'cat_2',
    categoryName: 'Shopping',
    categoryIcon: 'ShoppingBag',
    categoryColor: '#EC4899',
    paymentMethodId: 'pm_1',
    paymentMethodName: 'HDFC Credit Card',
    date: new Date(Date.now() - 86400000 * 4).toISOString(),
    notes: 'Summer sale discount purchase'
  },
  {
    id: 'exp_6',
    title: 'UI/UX Design Retainer',
    amount: 850.00,
    type: 'INCOME',
    categoryId: 'cat_8',
    categoryName: 'Freelance',
    categoryIcon: 'Laptop',
    categoryColor: '#6366F1',
    paymentMethodId: 'pm_4',
    paymentMethodName: 'ICICI Bank',
    date: new Date(Date.now() - 86400000 * 5).toISOString(),
    notes: 'Client project milestone 2'
  },
  {
    id: 'exp_7',
    title: 'Cinema Tickets & Popcorn',
    amount: 32.00,
    type: 'EXPENSE',
    categoryId: 'cat_5',
    categoryName: 'Entertainment',
    categoryIcon: 'Film',
    categoryColor: '#8B5CF6',
    paymentMethodId: 'pm_3',
    paymentMethodName: 'Cash',
    date: new Date(Date.now() - 3600000 * 12).toISOString(),
    notes: 'IMAX Movie night with friends'
  }
];

export const INITIAL_BUDGETS: Budget[] = [
  {
    id: 'bgt_1',
    categoryId: 'cat_1',
    categoryName: 'Food & Dining',
    categoryIcon: 'Utensils',
    categoryColor: '#EF4444',
    limitAmount: 500,
    spentAmount: 142.50,
    monthYear: '2026-08'
  },
  {
    id: 'bgt_2',
    categoryId: 'cat_2',
    categoryName: 'Shopping',
    categoryIcon: 'ShoppingBag',
    categoryColor: '#EC4899',
    limitAmount: 300,
    spentAmount: 129.99,
    monthYear: '2026-08'
  },
  {
    id: 'bgt_3',
    categoryId: 'cat_3',
    categoryName: 'Transport',
    categoryIcon: 'Car',
    categoryColor: '#3B82F6',
    limitAmount: 200,
    spentAmount: 24.80,
    monthYear: '2026-08'
  },
  {
    id: 'bgt_4',
    categoryId: 'cat_4',
    categoryName: 'Bills & Utilities',
    categoryIcon: 'Zap',
    categoryColor: '#F59E0B',
    limitAmount: 250,
    spentAmount: 88.00,
    monthYear: '2026-08'
  }
];
