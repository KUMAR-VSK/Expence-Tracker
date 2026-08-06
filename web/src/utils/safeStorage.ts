// Safe LocalStorage API Wrapper for Android WebView and Browser Environments
const memoryFallback: Record<string, string> = {};

export const safeStorage = {
  getItem: (key: string): string | null => {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        return window.localStorage.getItem(key);
      }
    } catch (err) {
      console.warn(`[safeStorage] getItem failed for key "${key}", using memory fallback`, err);
    }
    return memoryFallback[key] ?? null;
  },

  setItem: (key: string, value: string): void => {
    try {
      memoryFallback[key] = value;
      if (typeof window !== 'undefined' && window.localStorage) {
        window.localStorage.setItem(key, value);
      }
    } catch (err) {
      console.warn(`[safeStorage] setItem failed for key "${key}"`, err);
    }
  },

  removeItem: (key: string): void => {
    try {
      delete memoryFallback[key];
      if (typeof window !== 'undefined' && window.localStorage) {
        window.localStorage.removeItem(key);
      }
    } catch (err) {
      console.warn(`[safeStorage] removeItem failed for key "${key}"`, err);
    }
  },

  clear: (): void => {
    try {
      for (const k in memoryFallback) delete memoryFallback[k];
      if (typeof window !== 'undefined' && window.localStorage) {
        window.localStorage.clear();
      }
    } catch (err) {
      console.warn('[safeStorage] clear failed', err);
    }
  }
};
