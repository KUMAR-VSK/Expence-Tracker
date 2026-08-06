import React, { useState } from 'react';
import { X, FileSpreadsheet, Download, Upload, CheckCircle2, AlertCircle } from 'lucide-react';
import * as XLSX from 'xlsx';
import type { TransactionType } from '../types';

interface ParsedRow {
  date: string;
  title: string;
  amount: number;
  type: TransactionType;
  categoryName: string;
  paymentMethodName: string;
  notes?: string;
  isValid: boolean;
  error?: string;
}

interface BulkImportModalProps {
  isOpen: boolean;
  onClose: () => void;
  currency: string;
  onImportBulk: (transactions: Omit<ParsedRow, 'isValid' | 'error'>[]) => void;
}

export const BulkImportModal: React.FC<BulkImportModalProps> = ({
  isOpen,
  onClose,
  currency,
  onImportBulk
}) => {
  const [parsedRows, setParsedRows] = useState<ParsedRow[]>([]);
  const [fileName, setFileName] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);

  if (!isOpen) return null;

  // Download Sample Template CSV
  const handleDownloadSample = () => {
    const sampleContent = `Date,Title,Amount,Type,Category,PaymentMethod,Notes
2026-08-01,Supermarket Groceries,3500,EXPENSE,Food & Dining,Google Pay,Weekly grocery store
2026-08-02,Monthly Salary,85000,INCOME,Salary,Google Pay,August paycheck
2026-08-03,Electricity Bill,2200,EXPENSE,Bills & Utilities,Cash,Power bill payment
2026-08-04,Coffee Shop,250,EXPENSE,Food & Dining,Cash,Morning espresso`;

    const blob = new Blob([sampleContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'expense_import_template.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Process File Upload (.xlsx, .xls, .csv)
  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setFileName(file.name);
    setIsProcessing(true);

    const reader = new FileReader();
    reader.onload = (evt) => {
      try {
        const data = new Uint8Array(evt.target?.result as ArrayBuffer);
        const workbook = XLSX.read(data, { type: 'array' });
        const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
        const rawJson: any[] = XLSX.utils.sheet_to_json(firstSheet, { defval: '' });

        const rows: ParsedRow[] = rawJson.map((row) => {
          const rawDate = row['Date'] || row['date'] || new Date().toISOString().split('T')[0];
          const rawTitle = row['Title'] || row['title'] || row['Description'] || 'Imported Transaction';
          const rawAmount = parseFloat(row['Amount'] || row['amount'] || 0);
          const rawType = (row['Type'] || row['type'] || 'EXPENSE').toString().toUpperCase();
          const rawCat = row['Category'] || row['category'] || 'General';
          const rawPM = row['PaymentMethod'] || row['paymentmethod'] || row['Payment'] || 'Google Pay';
          const rawNotes = row['Notes'] || row['notes'] || '';

          const type: TransactionType = rawType === 'INCOME' ? 'INCOME' : 'EXPENSE';
          const isValid = !isNaN(rawAmount) && rawAmount > 0 && rawTitle.trim().length > 0;

          return {
            date: new Date(rawDate).toISOString().split('T')[0],
            title: rawTitle.toString().trim(),
            amount: isNaN(rawAmount) ? 0 : rawAmount,
            type,
            categoryName: rawCat.toString().trim(),
            paymentMethodName: rawPM.toString().trim(),
            notes: rawNotes.toString().trim() || undefined,
            isValid,
            error: !isValid ? 'Invalid amount or missing title' : undefined
          };
        });

        setParsedRows(rows);
      } catch (err) {
        console.error('Error parsing excel file:', err);
      } finally {
        setIsProcessing(false);
      }
    };
    reader.readAsArrayBuffer(file);
  };

  const validRows = parsedRows.filter(r => r.isValid);

  const handleConfirmImport = () => {
    if (validRows.length === 0) return;

    onImportBulk(validRows.map(r => ({
      date: new Date(r.date).toISOString(),
      title: r.title,
      amount: r.amount,
      type: r.type,
      categoryName: r.categoryName,
      paymentMethodName: r.paymentMethodName,
      notes: r.notes
    })));

    setParsedRows([]);
    setFileName('');
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
        maxWidth: 480,
        maxHeight: '90vh',
        background: '#1E293B',
        padding: 24,
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        gap: 16
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
          <FileSpreadsheet size={22} style={{ color: '#10B981' }} /> Bulk Import Excel / CSV
        </h3>

        {/* Action Buttons */}
        <div style={{ display: 'flex', gap: 8 }}>
          <button
            onClick={handleDownloadSample}
            style={{
              flex: 1,
              background: 'rgba(99, 102, 241, 0.12)',
              border: '1px solid rgba(99, 102, 241, 0.3)',
              borderRadius: 10,
              padding: '8px 12px',
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
            <Download size={14} /> Download Sample Template (.csv)
          </button>
        </div>

        {/* Upload Box */}
        <label style={{
          border: '2px dashed rgba(255, 255, 255, 0.15)',
          borderRadius: 14,
          padding: 20,
          textAlign: 'center',
          cursor: 'pointer',
          background: 'rgba(255, 255, 255, 0.02)',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: 8
        }}>
          <Upload size={24} style={{ color: '#10B981' }} />
          <div style={{ fontSize: 13, fontWeight: 600, color: '#FFF' }}>
            {fileName ? fileName : 'Click to select Excel (.xlsx, .xls) or CSV file'}
          </div>
          <div style={{ fontSize: 11, color: '#94A3B8' }}>Supports headers: Date, Title, Amount, Type, Category, PaymentMethod</div>
          <input
            type="file"
            accept=".xlsx, .xls, .csv"
            onChange={handleFileUpload}
            style={{ display: 'none' }}
          />
        </label>

        {/* Preview List */}
        {parsedRows.length > 0 && (
          <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 8, maxHeight: 220 }}>
            <div style={{ fontSize: 12, fontWeight: 700, color: '#94A3B8', display: 'flex', justifyContent: 'space-between' }}>
              <span>Detected Rows ({parsedRows.length})</span>
              <span style={{ color: '#10B981' }}>{validRows.length} Valid</span>
            </div>

            {parsedRows.map((r, idx) => (
              <div
                key={idx}
                style={{
                  background: r.isValid ? 'rgba(16, 185, 129, 0.08)' : 'rgba(239, 68, 68, 0.08)',
                  border: r.isValid ? '1px solid rgba(16, 185, 129, 0.2)' : '1px solid rgba(239, 68, 68, 0.2)',
                  borderRadius: 10,
                  padding: '8px 12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  fontSize: 12
                }}
              >
                <div>
                  <div style={{ fontWeight: 700, color: '#FFF', display: 'flex', alignItems: 'center', gap: 6 }}>
                    {r.isValid ? <CheckCircle2 size={13} style={{ color: '#10B981' }} /> : <AlertCircle size={13} style={{ color: '#EF4444' }} />}
                    {r.title}
                  </div>
                  <div style={{ color: '#94A3B8', fontSize: 10 }}>
                    {r.date} • {r.categoryName} • {r.paymentMethodName}
                  </div>
                </div>

                <div style={{ textAlign: 'right', fontWeight: 700, color: r.type === 'INCOME' ? '#10B981' : '#EF4444' }}>
                  {r.type === 'INCOME' ? '+' : '-'}{currency}{r.amount.toFixed(2)}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Action Button */}
        <button
          onClick={handleConfirmImport}
          disabled={validRows.length === 0 || isProcessing}
          className="btn-primary"
          style={{
            opacity: validRows.length === 0 ? 0.5 : 1,
            cursor: validRows.length === 0 ? 'not-allowed' : 'pointer'
          }}
        >
          Import {validRows.length} Valid Transactions
        </button>
      </div>
    </div>
  );
};
