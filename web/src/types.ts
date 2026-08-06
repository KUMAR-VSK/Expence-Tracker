export type TransactionType = 'EXPENSE' | 'INCOME';

export interface Category {
  id: string;
  name: string;
  icon: string;
  color: string;
  type: TransactionType;
}

export interface PaymentMethod {
  id: string;
  name: string;
  type: 'CARD' | 'CASH' | 'UPI' | 'BANK';
  icon: string;
  accountNumber?: string;
}

export interface Expense {
  id: string;
  title: string;
  amount: number;
  type: TransactionType;
  categoryId: string;
  categoryName: string;
  categoryIcon: string;
  categoryColor: string;
  paymentMethodId: string;
  paymentMethodName: string;
  date: string; // ISO string
  notes?: string;
  hasAudioNote?: boolean;
  isRecurring?: boolean;
  recurringFrequency?: 'MONTHLY' | 'WEEKLY' | 'YEARLY';
}

export interface Budget {
  id: string;
  categoryId: string;
  categoryName: string;
  categoryIcon: string;
  categoryColor: string;
  limitAmount: number;
  spentAmount: number;
  monthYear: string;
}

export interface Subscription {
  id: string;
  name: string;
  amount: number;
  categoryName: string;
  billingCycle: 'Monthly' | 'Yearly';
  dueDate: string;
  icon: string;
  active: boolean;
}

export interface SavingGoal {
  id: string;
  title: string;
  targetAmount: number;
  currentAmount: number;
  targetDate: string;
  icon: string;
  color: string;
}

export interface AppSettings {
  currency: string; // '₹', '$', '€', '£'
  darkMode: boolean;
  isPinLocked: boolean;
  pin: string;
  viewMode: 'PHONE_FRAME' | 'MINI_PLAYER' | 'FULL_SCREEN';
  userName: string;
}
