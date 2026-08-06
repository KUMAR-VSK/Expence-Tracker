import React, { useState } from 'react';
import { X, Coins, Lock, Unlock, Download, Upload, AlertCircle, CheckCircle2, User } from 'lucide-react';
import type { AppSettings, Expense, Category, PaymentMethod, Budget, Subscription } from '../types';

interface BackupData {
  expenses: Expense[];
  categories: Category[];
  paymentMethods: PaymentMethod[];
  budgets: Budget[];
  subscriptions: Subscription[];
  settings: AppSettings;
}

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  settings: AppSettings;
  onUpdateSettings: (patch: Partial<AppSettings>) => void;
  backupData: BackupData;
  onImport: (data: BackupData) => void;
}

const CURRENCIES = ['₹', '$', '€', '£'] as const;
const isRecord = (value: unknown): value is Record<string, unknown> => typeof value === 'object' && value !== null;
const isString = (value: unknown): value is string => typeof value === 'string';
const isNumber = (value: unknown): value is number => typeof value === 'number' && Number.isFinite(value);
const hasStrings = (value: Record<string, unknown>, fields: string[]) => fields.every(field => isString(value[field]));
const isTransactionType = (value: unknown) => value === 'EXPENSE' || value === 'INCOME';

const isExpense = (value: unknown): value is Expense => isRecord(value)
  && hasStrings(value, ['id', 'title', 'categoryId', 'categoryName', 'categoryIcon', 'categoryColor', 'paymentMethodId', 'paymentMethodName', 'date'])
  && isNumber(value.amount)
  && isTransactionType(value.type);

const isCategory = (value: unknown): value is Category => isRecord(value)
  && hasStrings(value, ['id', 'name', 'icon', 'color'])
  && isTransactionType(value.type);

const isPaymentMethod = (value: unknown): value is PaymentMethod => isRecord(value)
  && hasStrings(value, ['id', 'name', 'icon'])
  && (value.type === 'CARD' || value.type === 'CASH' || value.type === 'UPI' || value.type === 'BANK');

const isBudget = (value: unknown): value is Budget => isRecord(value)
  && hasStrings(value, ['id', 'categoryId', 'categoryName', 'categoryIcon', 'categoryColor', 'monthYear'])
  && isNumber(value.limitAmount)
  && isNumber(value.spentAmount);

const isSubscription = (value: unknown): value is Subscription => isRecord(value)
  && hasStrings(value, ['id', 'name', 'categoryName', 'billingCycle', 'dueDate', 'icon'])
  && isNumber(value.amount)
  && (value.billingCycle === 'Monthly' || value.billingCycle === 'Yearly')
  && typeof value.active === 'boolean';

const isAppSettings = (value: unknown): value is AppSettings => isRecord(value)
  && isString(value.currency)
  && typeof value.darkMode === 'boolean'
  && typeof value.isPinLocked === 'boolean'
  && isString(value.pin)
  && isString(value.userName)
  && (value.viewMode === 'PHONE_FRAME' || value.viewMode === 'MINI_PLAYER' || value.viewMode === 'FULL_SCREEN');

const isBackupData = (value: unknown): value is BackupData => isRecord(value)
  && Array.isArray(value.expenses) && value.expenses.every(isExpense)
  && Array.isArray(value.categories) && value.categories.some(category => category.type === 'EXPENSE') && value.categories.some(category => category.type === 'INCOME') && value.categories.every(isCategory)
  && Array.isArray(value.paymentMethods) && value.paymentMethods.length > 0 && value.paymentMethods.every(isPaymentMethod)
  && Array.isArray(value.budgets) && value.budgets.every(isBudget)
  && Array.isArray(value.subscriptions) && value.subscriptions.every(isSubscription)
  && isAppSettings(value.settings);

export const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  onClose,
  settings,
  onUpdateSettings,
  backupData,
  onImport
}) => {
  const [pinInput, setPinInput] = useState('');
  const [pinError, setPinError] = useState('');
  const [importStatus, setImportStatus] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  if (!isOpen) return null;

  const handleExport = () => {
    const data = JSON.stringify(backupData, null, 2);
    const blob = new Blob([data], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `expense-tracker-backup-${new Date().toISOString().slice(0, 10)}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const handleFileImport = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (evt) => {
      try {
        const content = evt.target?.result as string;
        const parsed = JSON.parse(content);
        if (!isBackupData(parsed)) {
          throw new Error('Invalid backup format');
        }
        onImport(parsed);
        setImportStatus({ type: 'success', message: 'Import successful! Data restored.' });
      } catch {
        setImportStatus({ type: 'error', message: 'Failed to import: invalid or corrupted file' });
      }
    };
    reader.readAsText(file);
    e.target.value = '';
  };

  const handleEnablePin = () => {
    const pin = pinInput.trim();
    if (!/^\d{4}$/.test(pin)) {
      setPinError('Enter a 4-digit PIN');
      return;
    }
    onUpdateSettings({ pin, isPinLocked: true });
    setPinInput('');
    setPinError('');
  };

  const handleDisablePin = () => {
    if (pinInput !== settings.pin) {
      setPinError('Incorrect PIN');
      return;
    }
    onUpdateSettings({ isPinLocked: false });
    setPinInput('');
    setPinError('');
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
        maxHeight: '90vh',
        background: '#1E293B',
        padding: 24,
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        gap: 20,
        overflowY: 'auto'
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

        <h3 style={{ fontSize: 18, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 8, color: '#FFF' }}>
          <Coins size={22} style={{ color: '#10B981' }} /> Settings
        </h3>

        {/* User Name */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <div style={{ fontSize: 13, fontWeight: 700, color: '#94A3B8' }}>Your Name</div>
          <input
            type="text"
            placeholder="Enter your name"
            value={settings.userName}
            onChange={(e) => onUpdateSettings({ userName: e.target.value })}
            maxLength={30}
            style={{
              background: 'rgba(255, 255, 255, 0.06)',
              border: '1px solid rgba(255, 255, 255, 0.12)',
              borderRadius: 10,
              padding: '12px 14px',
              color: '#FFF',
              fontSize: 15,
              outline: 'none'
            }}
          />
        </div>

        {/* Currency */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <div style={{ fontSize: 13, fontWeight: 700, color: '#94A3B8' }}>Currency Symbol</div>
          <div style={{ display: 'flex', gap: 8 }}>
            {CURRENCIES.map(curr => (
              <button
                key={curr}
                onClick={() => onUpdateSettings({ currency: curr })}
                style={{
                  flex: 1,
                  padding: '10px 0',
                  borderRadius: 10,
                  cursor: 'pointer',
                  fontSize: 18,
                  fontWeight: 700,
                  background: settings.currency === curr ? 'rgba(99, 102, 241, 0.2)' : 'rgba(255, 255, 255, 0.06)',
                  border: settings.currency === curr ? '1px solid #6366F1' : '1px solid rgba(255, 255, 255, 0.1)',
                  color: settings.currency === curr ? '#818CF8' : '#F8FAFC'
                }}
              >
                {curr}
              </button>
            ))}
          </div>
        </div>

        {/* PIN Lock */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10, borderTop: '1px solid rgba(255, 255, 255, 0.08)', paddingTop: 16 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 13, fontWeight: 700, color: '#F8FAFC' }}>
              {settings.isPinLocked ? <Lock size={16} style={{ color: '#10B981' }} /> : <Unlock size={16} style={{ color: '#94A3B8' }} />}
              PIN Lock {settings.isPinLocked ? 'Enabled' : 'Disabled'}
            </div>
          </div>

          <div style={{ display: 'flex', gap: 8 }}>
            <input
              type="password"
              inputMode="numeric"
              maxLength={4}
              placeholder={settings.isPinLocked ? 'Enter current PIN to disable' : 'Set a 4-digit PIN'}
              value={pinInput}
              onChange={(e) => {
                setPinInput(e.target.value.replace(/\D/g, ''));
                setPinError('');
              }}
              style={{
                flex: 1,
                background: 'rgba(255, 255, 255, 0.06)',
                border: '1px solid rgba(255, 255, 255, 0.12)',
                borderRadius: 10,
                padding: '10px 12px',
                color: '#FFF',
                fontSize: 14,
                letterSpacing: 4,
                outline: 'none'
              }}
            />
            {settings.isPinLocked ? (
              <button
                onClick={handleDisablePin}
                className="btn-primary"
                style={{ padding: '10px 16px', fontSize: 13 }}
              >
                Disable
              </button>
            ) : (
              <button
                onClick={handleEnablePin}
                className="btn-primary"
                style={{ padding: '10px 16px', fontSize: 13 }}
              >
                Enable
              </button>
            )}
          </div>

          {pinError && <div style={{ fontSize: 12, color: '#EF4444' }}>{pinError}</div>}
        </div>

        {/* Backup & Restore */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10, borderTop: '1px solid rgba(255, 255, 255, 0.08)', paddingTop: 16 }}>
          <div style={{ fontSize: 13, fontWeight: 700, color: '#94A3B8' }}>Backup & Restore</div>
          <div style={{ display: 'flex', gap: 8 }}>
            <button
              onClick={handleExport}
              style={{
                flex: 1,
                background: 'rgba(99, 102, 241, 0.1)',
                border: '1px solid rgba(99, 102, 241, 0.3)',
                borderRadius: 10,
                padding: '10px 12px',
                color: '#818CF8',
                fontSize: 12,
                fontWeight: 700,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 6
              }}
            >
              <Download size={14} /> Export Data (.json)
            </button>
            <label style={{
              flex: 1,
              background: 'rgba(16, 185, 129, 0.1)',
              border: '1px solid rgba(16, 185, 129, 0.3)',
              borderRadius: 10,
              padding: '10px 12px',
              color: '#34D399',
              fontSize: 12,
              fontWeight: 700,
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 6
            }}>
              <Upload size={14} /> Import Data
              <input
                type="file"
                accept=".json"
                onChange={handleFileImport}
                style={{ display: 'none' }}
              />
            </label>
          </div>
          {importStatus && <div style={{ fontSize: 12, color: importStatus.type === 'success' ? '#10B981' : '#EF4444', display: 'flex', alignItems: 'center', gap: 6 }}>
            {importStatus.type === 'success' ? <CheckCircle2 size={13} /> : <AlertCircle size={13} />}
            {importStatus.message}
          </div>}
        </div>
      </div>
    </div>
  );
};
