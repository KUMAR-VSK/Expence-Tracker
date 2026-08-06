import { useEffect, useState } from 'react';
import { safeStorage } from './safeStorage';

export function usePersistentState<T>(
  key: string,
  defaultValue: T,
  validate: (value: unknown) => T = (value) => value as T
): [T, React.Dispatch<React.SetStateAction<T>>] {
  const [state, setState] = useState<T>(() => {
    const raw = safeStorage.getItem(key);
    if (raw !== null) {
      try {
        const parsed: unknown = JSON.parse(raw);
        return validate(parsed);
      } catch (err) {
        console.warn(`[usePersistentState] corrupt data for "${key}", falling back to default`, err);
      }
    }
    return defaultValue;
  });

  useEffect(() => {
    safeStorage.setItem(key, JSON.stringify(state));
  }, [key, state]);

  return [state, setState];
}

export function isArray<T>(value: unknown): value is T[] {
  return Array.isArray(value);
}
