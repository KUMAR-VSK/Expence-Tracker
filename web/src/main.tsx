import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

console.log('[ExpenseTracker] main.tsx loaded, creating root...');
window.addEventListener('error', (e) => console.error('[ExpenseTracker] Window error:', e.message, e.filename, e.lineno, e.colno, e.error));
window.addEventListener('unhandledrejection', (e) => console.error('[ExpenseTracker] Unhandled rejection:', e.reason));

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
