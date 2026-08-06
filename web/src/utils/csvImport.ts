import type { TransactionType } from '../types';

export const MAX_CSV_FILE_SIZE = 1024 * 1024;
const MAX_CSV_ROWS = 1_000;

export interface ParsedCsvTransaction {
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

interface CsvDefaults {
  expenseCategory: string;
  incomeCategory: string;
  paymentMethod: string;
}

const today = () => new Date().toISOString().slice(0, 10);

const parseCsvTable = (content: string): string[][] => {
  const rows: string[][] = [];
  let row: string[] = [];
  let field = '';
  let inQuotes = false;

  for (let index = 0; index < content.length; index += 1) {
    const character = content[index];

    if (character === '"') {
      if (inQuotes && content[index + 1] === '"') {
        field += '"';
        index += 1;
      } else {
        inQuotes = !inQuotes;
      }
    } else if (character === ',' && !inQuotes) {
      row.push(field.trim());
      field = '';
    } else if ((character === '\n' || character === '\r') && !inQuotes) {
      if (character === '\r' && content[index + 1] === '\n') index += 1;
      row.push(field.trim());
      if (row.some(value => value.length > 0)) rows.push(row);
      row = [];
      field = '';
    } else {
      field += character;
    }
  }

  if (inQuotes) throw new Error('The CSV contains an unterminated quoted field.');

  row.push(field.trim());
  if (row.some(value => value.length > 0)) rows.push(row);
  return rows;
};

const getValue = (row: string[], headers: string[], aliases: string[]) => {
  const index = headers.findIndex(header => aliases.includes(header));
  return index === -1 ? '' : row[index] || '';
};

export const parseCsvTransactions = (content: string, defaults: CsvDefaults): ParsedCsvTransaction[] => {
  if (content.length > MAX_CSV_FILE_SIZE) {
    throw new Error('CSV files must be 1 MB or smaller.');
  }

  const [headerRow, ...dataRows] = parseCsvTable(content);
  if (!headerRow) throw new Error('The CSV file is empty.');
  if (dataRows.length > MAX_CSV_ROWS) throw new Error(`CSV files can contain at most ${MAX_CSV_ROWS} transactions.`);

  const headers = headerRow.map(header => header.replace(/^\uFEFF/, '').toLowerCase().replace(/\s/g, ''));

  return dataRows.map(row => {
    const rawDate = getValue(row, headers, ['date']);
    const rawTitle = getValue(row, headers, ['title', 'description']);
    const rawAmount = getValue(row, headers, ['amount']);
    const rawType = getValue(row, headers, ['type']).toUpperCase();
    const rawCategory = getValue(row, headers, ['category']);
    const rawPaymentMethod = getValue(row, headers, ['paymentmethod', 'payment']);
    const rawNotes = getValue(row, headers, ['notes', 'note']);
    const parsedDate = rawDate ? new Date(rawDate) : new Date();
    const amount = Number(rawAmount.replace(/,/g, ''));
    const type: TransactionType = rawType === 'INCOME' ? 'INCOME' : 'EXPENSE';
    const errors: string[] = [];

    if (!rawTitle) errors.push('missing title');
    if (!Number.isFinite(amount) || amount <= 0) errors.push('invalid amount');
    if (rawType && rawType !== 'INCOME' && rawType !== 'EXPENSE') errors.push('invalid type');
    if (Number.isNaN(parsedDate.getTime())) errors.push('invalid date');

    return {
      date: Number.isNaN(parsedDate.getTime()) ? today() : parsedDate.toISOString().slice(0, 10),
      title: rawTitle,
      amount: Number.isFinite(amount) ? amount : 0,
      type,
      categoryName: rawCategory || (type === 'INCOME' ? defaults.incomeCategory : defaults.expenseCategory),
      paymentMethodName: rawPaymentMethod || defaults.paymentMethod,
      notes: rawNotes || undefined,
      isValid: errors.length === 0,
      error: errors.length ? `Row has ${errors.join(', ')}.` : undefined
    };
  });
};
