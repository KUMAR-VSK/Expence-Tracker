import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { ErrorBoundary } from 'react-error-boundary'
import './index.css'
import App from './App.tsx'

console.log('[ExpenseTracker] main.tsx loaded, creating root...');
window.addEventListener('error', (e) => console.error('[ExpenseTracker] Window error:', e.message, e.filename, e.lineno, e.colno, e.error));
window.addEventListener('unhandledrejection', (e) => console.error('[ExpenseTracker] Unhandled rejection:', e.reason));

// Fallback: if React doesn't mount within 3s, show error
setTimeout(() => {
  const root = document.getElementById('root');
  if (root && root.children.length === 0) {
    console.error('[ExpenseTracker] React failed to mount - root is empty after 3s');
    root.innerHTML = `
      <div style="padding:20px;color:#EF4444;font-family:system-ui;text-align:center;">
        <h2>App failed to load</h2>
        <p>Check console for errors (chrome://inspect)</p>
        <pre id="log" style="text-align:left;background:#1E293B;padding:10px;border-radius:8px;max-height:300px;overflow:auto;"></pre>
      </div>
    `;
    const originalLog = console.log;
    const originalError = console.error;
    const logEl = document.getElementById('log');
    console.log = (...args) => { originalLog(...args); if (logEl) logEl.textContent += '[LOG] ' + args.join(' ') + '\n'; };
    console.error = (...args) => { originalError(...args); if (logEl) logEl.textContent += '[ERROR] ' + args.join(' ') + '\n'; };
  }
}, 3000);

function ErrorFallback({ error, resetErrorBoundary }: { error: unknown; resetErrorBoundary: () => void }) {
  return (
    <div style={{ padding: '20px', color: '#EF4444', fontFamily: 'system-ui', textAlign: 'center' }}>
      <h2>Something went wrong</h2>
      <pre style={{ textAlign: 'left', background: '#1E293B', padding: '10px', borderRadius: '8px', maxHeight: '300px', overflow: 'auto', color: '#FFF' }}>
        {String(error)}\n{error instanceof Error ? error.stack : ''}
      </pre>
      <button onClick={resetErrorBoundary} style={{ marginTop: '10px', padding: '8px 16px', background: '#6366F1', color: '#FFF', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>
        Try again
      </button>
    </div>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ErrorBoundary FallbackComponent={ErrorFallback} onReset={() => window.location.reload()}>
      <App />
    </ErrorBoundary>
  </StrictMode>,
)