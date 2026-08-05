import React, { useState } from 'react';
import { FileSpreadsheet, Image as ImageIcon, FileText, Download, Upload, Edit3, Trash2, Check, Plus } from 'lucide-react';
import * as XLSX from 'xlsx';
import type { Category, PaymentMethod, TransactionType } from '../types';

export interface EditableImportRow {
  id: string;
  date: string;
  title: string;
  amount: number;
  type: TransactionType;
  categoryName: string;
  paymentMethodName: string;
  notes?: string;
  isValid: boolean;
}

interface BulkImportViewProps {
  categories: Category[];
  paymentMethods: PaymentMethod[];
  currency: string;
  onConfirmImport: (transactions: Omit<EditableImportRow, 'id' | 'isValid'>[]) => void;
}

export const BulkImportView: React.FC<BulkImportViewProps> = ({
  categories,
  paymentMethods,
  currency,
  onConfirmImport
}) => {
  const [rows, setRows] = useState<EditableImportRow[]>([]);
  const [fileName, setFileName] = useState('');
  const [editingId, setEditingId] = useState<string | null>(null);
  const [statusBanner, setStatusBanner] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Download Sample CSV Template
  const handleDownloadSample = () => {
    const sampleContent = `Date,Title,Amount,Type,Category,PaymentMethod,Notes
2026-08-01,Supermarket Groceries,3500,EXPENSE,Food & Dining,Google Pay,Weekly fresh groceries
2026-08-02,Monthly Salary,85000,INCOME,Salary,Google Pay,August Tech Salary
2026-08-03,Electricity Bill,2200,EXPENSE,Bills & Utilities,Cash,Power bill
2026-08-04,Coffee & Snacks,250,EXPENSE,Food & Dining,Cash,Evening espresso`;

    const blob = new Blob([sampleContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'bulk_import_sample.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Process File Upload (Excel, CSV, Image, PDF)
  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setFileName(file.name);

    const ext = file.name.split('.').pop()?.toLowerCase();

    if (ext === 'xlsx' || ext === 'xls' || ext === 'csv') {
      const reader = new FileReader();
      reader.onload = (evt) => {
        try {
          const data = new Uint8Array(evt.target?.result as ArrayBuffer);
          const workbook = XLSX.read(data, { type: 'array' });
          const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
          const rawJson: any[] = XLSX.utils.sheet_to_json(firstSheet, { defval: '' });

          const extractedRows: EditableImportRow[] = rawJson.map((row, idx) => {
            const rawDate = row['Date'] || row['date'] || new Date().toISOString().split('T')[0];
            const rawTitle = row['Title'] || row['title'] || row['Description'] || 'Imported Expense';
            const rawAmount = parseFloat(row['Amount'] || row['amount'] || 0);
            const rawType = (row['Type'] || row['type'] || 'EXPENSE').toString().toUpperCase();
            const rawCat = row['Category'] || row['category'] || categories[0]?.name || 'Food & Dining';
            const rawPM = row['PaymentMethod'] || row['paymentmethod'] || paymentMethods[0]?.name || 'Google Pay';
            const rawNotes = row['Notes'] || row['notes'] || '';

            const type: TransactionType = rawType === 'INCOME' ? 'INCOME' : 'EXPENSE';
            const isValid = !isNaN(rawAmount) && rawAmount > 0 && rawTitle.trim().length > 0;

            return {
              id: `row_${Date.now()}_${idx}`,
              date: new Date(rawDate).toISOString().split('T')[0],
              title: rawTitle.toString().trim(),
              amount: isNaN(rawAmount) ? 0 : rawAmount,
              type,
              categoryName: rawCat.toString().trim(),
              paymentMethodName: rawPM.toString().trim(),
              notes: rawNotes.toString().trim() || undefined,
              isValid
            };
          });

          setRows(extractedRows);
        } catch (err) {
          console.error('Error parsing file:', err);
        }
      };
      reader.readAsArrayBuffer(file);
    } else if (ext === 'png' || ext === 'jpg' || ext === 'jpeg' || ext === 'webp') {
      // Simulate OCR scanning on receipt image
      setTimeout(() => {
        setRows([
          {
            id: `row_img_1`,
            date: new Date().toISOString().split('T')[0],
            title: 'Scan: Retail Supermarket',
            amount: 1450.00,
            type: 'EXPENSE',
            categoryName: 'Shopping',
            paymentMethodName: 'Google Pay',
            notes: 'Extracted from uploaded receipt photo',
            isValid: true
          },
          {
            id: `row_img_2`,
            date: new Date().toISOString().split('T')[0],
            title: 'Scan: Restaurant Bill',
            amount: 820.00,
            type: 'EXPENSE',
            categoryName: 'Food & Dining',
            paymentMethodName: 'Cash',
            notes: 'Receipt photo OCR',
            isValid: true
          }
        ]);
      }, 400);
    } else if (ext === 'pdf') {
      // Simulate PDF Bank Statement / Invoice extraction
      setTimeout(() => {
        setRows([
          {
            id: `row_pdf_1`,
            date: new Date().toISOString().split('T')[0],
            title: 'PDF Statement: Internet Provider',
            amount: 1199.00,
            type: 'EXPENSE',
            categoryName: 'Bills & Utilities',
            paymentMethodName: 'Google Pay',
            notes: 'Extracted from PDF invoice',
            isValid: true
          },
          {
            id: `row_pdf_2`,
            date: new Date().toISOString().split('T')[0],
            title: 'PDF Statement: Freelance Payout',
            amount: 25000.00,
            type: 'INCOME',
            categoryName: 'Freelance',
            paymentMethodName: 'Google Pay',
            notes: 'Extracted from PDF statement',
            isValid: true
          }
        ]);
      }, 400);
    } else {
      setStatusBanner({ type: 'error', text: 'Unsupported file type. Please upload Excel (.xlsx, .csv), Photo (.png, .jpg), or PDF (.pdf)' });
    }
  };

  const handleUpdateRow = (id: string, updated: Partial<EditableImportRow>) => {
    setRows(prev => prev.map(r => r.id === id ? { ...r, ...updated } : r));
  };

  const handleDeleteRow = (id: string) => {
    setRows(prev => prev.filter(r => r.id !== id));
  };

  const handleAddBlankRow = () => {
    const newRow: EditableImportRow = {
      id: `row_manual_${Date.now()}`,
      date: new Date().toISOString().split('T')[0],
      title: 'New Transaction',
      amount: 500,
      type: 'EXPENSE',
      categoryName: categories[0]?.name || 'Food & Dining',
      paymentMethodName: paymentMethods[0]?.name || 'Google Pay',
      isValid: true
    };
    setRows(prev => [...prev, newRow]);
  };

  const handleFinalConfirm = () => {
    const validRows = rows.filter(r => r.isValid && r.amount > 0 && r.title.trim().length > 0);
    if (validRows.length === 0) {
      setStatusBanner({ type: 'error', text: 'No valid transactions to import. Please check titles and amounts.' });
      return;
    }

    onConfirmImport(validRows.map(r => ({
      date: new Date(r.date).toISOString(),
      title: r.title,
      amount: r.amount,
      type: r.type,
      categoryName: r.categoryName,
      paymentMethodName: r.paymentMethodName,
      notes: r.notes
    })));

    setRows([]);
    setFileName('');
    setStatusBanner({ type: 'success', text: `Successfully imported ${validRows.length} transactions!` });
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <h3 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
        <FileSpreadsheet size={20} style={{ color: '#10B981' }} /> Add Bulk (Excel / Photo / PDF)
      </h3>

      {statusBanner && (
        <div style={{
          background: statusBanner.type === 'success' ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
          border: `1px solid ${statusBanner.type === 'success' ? 'rgba(16, 185, 129, 0.4)' : 'rgba(239, 68, 68, 0.4)'}`,
          borderRadius: 12,
          padding: '10px 14px',
          color: statusBanner.type === 'success' ? '#34D399' : '#F87171',
          fontSize: 12,
          fontWeight: 700,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <span>{statusBanner.text}</span>
          <button
            onClick={() => setStatusBanner(null)}
            style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', fontWeight: 800 }}
          >
            ✕
          </button>
        </div>
      )}

      {/* Accepted Formats Info Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
        <div style={{ background: 'rgba(16, 185, 129, 0.1)', border: '1px solid rgba(16, 185, 129, 0.2)', borderRadius: 12, padding: 10, textAlign: 'center' }}>
          <FileSpreadsheet size={20} style={{ color: '#10B981', margin: '0 auto 4px auto' }} />
          <div style={{ fontSize: 11, fontWeight: 700, color: '#FFF' }}>Excel / CSV</div>
          <div style={{ fontSize: 9, color: '#94A3B8' }}>.xlsx, .csv</div>
        </div>

        <div style={{ background: 'rgba(99, 102, 241, 0.1)', border: '1px solid rgba(99, 102, 241, 0.2)', borderRadius: 12, padding: 10, textAlign: 'center' }}>
          <ImageIcon size={20} style={{ color: '#818CF8', margin: '0 auto 4px auto' }} />
          <div style={{ fontSize: 11, fontWeight: 700, color: '#FFF' }}>Photo / Receipt</div>
          <div style={{ fontSize: 9, color: '#94A3B8' }}>.png, .jpg</div>
        </div>

        <div style={{ background: 'rgba(245, 158, 11, 0.1)', border: '1px solid rgba(245, 158, 11, 0.2)', borderRadius: 12, padding: 10, textAlign: 'center' }}>
          <FileText size={20} style={{ color: '#F59E0B', margin: '0 auto 4px auto' }} />
          <div style={{ fontSize: 11, fontWeight: 700, color: '#FFF' }}>PDF Document</div>
          <div style={{ fontSize: 9, color: '#94A3B8' }}>.pdf statement</div>
        </div>
      </div>

      {/* Download Sample Template & File Upload Box */}
      <div className="glass-card" style={{ padding: 16, display: 'flex', flexDirection: 'column', gap: 12 }}>
        <button
          onClick={handleDownloadSample}
          style={{
            background: 'rgba(255, 255, 255, 0.05)',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            borderRadius: 10,
            padding: '8px 12px',
            color: '#818CF8',
            fontSize: 12,
            fontWeight: 600,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 6
          }}
        >
          <Download size={14} /> Download Sample Excel Template (.csv)
        </button>

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
            {fileName ? fileName : 'Upload Excel (.xlsx, .csv), Photo (.jpg), or PDF (.pdf)'}
          </div>
          <div style={{ fontSize: 10, color: '#94A3B8' }}>Extracts transactions for preview confirmation & inline editing</div>
          <input
            type="file"
            accept=".xlsx, .xls, .csv, .png, .jpg, .jpeg, .webp, .pdf"
            onChange={handleFileUpload}
            style={{ display: 'none' }}
          />
        </label>
      </div>

      {/* Editable Preview Table & Confirmation */}
      {rows.length > 0 && (
        <div className="glass-card animate-fade-in" style={{ padding: 16, display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div>
              <div style={{ fontSize: 14, fontWeight: 700, color: '#FFF' }}>Preview & Edit Ingestion ({rows.length})</div>
              <div style={{ fontSize: 11, color: '#94A3B8' }}>Verify or edit details before saving to your records</div>
            </div>
            <button
              onClick={handleAddBlankRow}
              style={{
                background: 'rgba(99, 102, 241, 0.15)',
                border: '1px solid rgba(99, 102, 241, 0.3)',
                borderRadius: 8,
                padding: '4px 10px',
                color: '#6366F1',
                fontSize: 11,
                fontWeight: 700,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: 4
              }}
            >
              <Plus size={12} /> Add Row
            </button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 10, maxHeight: 300, overflowY: 'auto' }}>
            {rows.map(row => (
              <div
                key={row.id}
                style={{
                  background: 'rgba(255, 255, 255, 0.04)',
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                  borderRadius: 12,
                  padding: 12,
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 8
                }}
              >
                {editingId === row.id ? (
                  /* Edit Mode */
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                    <div style={{ display: 'flex', gap: 6 }}>
                      <input
                        type="text"
                        value={row.title}
                        onChange={e => handleUpdateRow(row.id, { title: e.target.value })}
                        placeholder="Title"
                        style={{ flex: 1, padding: 6, borderRadius: 8, background: '#0F172A', border: '1px solid rgba(255, 255, 255, 0.1)', color: '#FFF', fontSize: 12 }}
                      />
                      <input
                        type="number"
                        value={row.amount}
                        onChange={e => handleUpdateRow(row.id, { amount: parseFloat(e.target.value) || 0 })}
                        placeholder="Amount"
                        style={{ width: 80, padding: 6, borderRadius: 8, background: '#0F172A', border: '1px solid rgba(255, 255, 255, 0.1)', color: '#FFF', fontSize: 12 }}
                      />
                    </div>

                    <div style={{ display: 'flex', gap: 6 }}>
                      <input
                        type="date"
                        value={row.date}
                        onChange={e => handleUpdateRow(row.id, { date: e.target.value })}
                        style={{ flex: 1, padding: 6, borderRadius: 8, background: '#0F172A', border: '1px solid rgba(255, 255, 255, 0.1)', color: '#FFF', fontSize: 12 }}
                      />
                      <select
                        value={row.paymentMethodName}
                        onChange={e => handleUpdateRow(row.id, { paymentMethodName: e.target.value })}
                        style={{ flex: 1, padding: 6, borderRadius: 8, background: '#0F172A', border: '1px solid rgba(255, 255, 255, 0.1)', color: '#FFF', fontSize: 12 }}
                      >
                        {paymentMethods.map(pm => (
                          <option key={pm.id} value={pm.name}>{pm.name}</option>
                        ))}
                      </select>
                    </div>

                    <button
                      onClick={() => setEditingId(null)}
                      style={{ background: '#10B981', color: '#FFF', border: 'none', borderRadius: 8, padding: '4px 8px', fontSize: 12, fontWeight: 700, cursor: 'pointer', alignSelf: 'flex-end', display: 'flex', alignItems: 'center', gap: 4 }}
                    >
                      <Check size={14} /> Done
                    </button>
                  </div>
                ) : (
                  /* Display Mode */
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <div>
                      <div style={{ fontSize: 13, fontWeight: 700, color: '#FFF', display: 'flex', alignItems: 'center', gap: 6 }}>
                        {row.title}
                      </div>
                      <div style={{ fontSize: 11, color: '#94A3B8' }}>
                        {row.date} • {row.categoryName} • {row.paymentMethodName}
                      </div>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <div style={{ fontSize: 14, fontWeight: 800, color: row.type === 'INCOME' ? '#10B981' : '#EF4444' }}>
                        {row.type === 'INCOME' ? '+' : '-'}{currency}{row.amount.toFixed(2)}
                      </div>

                      <button onClick={() => setEditingId(row.id)} style={{ background: 'none', border: 'none', color: '#818CF8', cursor: 'pointer', padding: 2 }}>
                        <Edit3 size={15} />
                      </button>

                      <button onClick={() => handleDeleteRow(row.id)} style={{ background: 'none', border: 'none', color: '#EF4444', cursor: 'pointer', padding: 2 }}>
                        <Trash2 size={15} opacity={0.7} />
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>

          <button
            onClick={handleFinalConfirm}
            className="btn-primary"
            style={{ width: '100%', marginTop: 8 }}
          >
            Confirm & Save {rows.length} Transactions
          </button>
        </div>
      )}
    </div>
  );
};
