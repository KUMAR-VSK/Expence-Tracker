import React, { useState } from 'react';
import { Lock, ShieldCheck, Delete } from 'lucide-react';

interface LockScreenProps {
  pin: string;
  onUnlock: () => void;
}

export const LockScreen: React.FC<LockScreenProps> = ({ pin, onUnlock }) => {
  const [enteredPin, setEnteredPin] = useState('');
  const [error, setError] = useState(false);

  const pressDigit = (digit: string) => {
    if (enteredPin.length >= 4) return;
    const next = enteredPin + digit;
    setEnteredPin(next);
    setError(false);
    if (next.length === 4) {
      if (next === pin) {
        setTimeout(onUnlock, 150);
      } else {
        setError(true);
        setTimeout(() => setEnteredPin(''), 350);
      }
    }
  };

  const digits = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '', '0', ''];

  const keypadStyle = {
    width: 72,
    height: 72,
    borderRadius: 99,
    background: 'rgba(255, 255, 255, 0.07)',
    border: 'none' as const,
    color: '#FFF',
    fontSize: 24,
    fontWeight: 700,
    cursor: 'pointer'
  };

  return (
    <div style={{
      position: 'fixed',
      inset: 0,
      zIndex: 2000,
      background: '#0F172A',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      gap: 24,
      padding: 24
    }}>
      <div style={{
        width: 56,
        height: 56,
        borderRadius: 18,
        background: 'rgba(99, 102, 241, 0.15)',
        border: '1px solid rgba(99, 102, 241, 0.3)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#6366F1'
      }}>
        <Lock size={26} />
      </div>

      <div style={{ fontSize: 17, fontWeight: 800, color: '#FFF' }}>Expense Tracker</div>
      <div style={{ fontSize: 13, color: '#94A3B8', marginTop: -12 }}>
        {error ? 'Incorrect PIN' : 'Enter PIN to unlock'}
      </div>

      {/* Dots */}
      <div style={{ display: 'flex', gap: 12 }}>
        {[0, 1, 2, 3].map(i => (
          <div
            key={i}
            style={{
              width: 14,
              height: 14,
              borderRadius: 99,
              background: i < enteredPin.length ? '#6366F1' : 'rgba(255, 255, 255, 0.12)',
              border: i < enteredPin.length ? 'none' : '1px solid rgba(255, 255, 255, 0.15)',
              transition: 'all 0.15s'
            }}
          />
        ))}
      </div>

      {/* Keypad */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(3, 72px)',
        gap: 12,
        marginTop: 8
      }}>
        {digits.map((d, i) =>
          d === '' ? (
            <div key={i} />
          ) : (
            <button key={i} onClick={() => pressDigit(d)} style={keypadStyle}>
              {d}
            </button>
          )
        )}
        <button
          onClick={() => setEnteredPin(enteredPin.slice(0, -1))}
          style={{
            ...keypadStyle,
            background: 'transparent',
            color: '#94A3B8',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}
        >
          <Delete size={22} />
        </button>
        <div />
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 11, color: '#64748B' }}>
        <ShieldCheck size={12} /> PIN protected
      </div>
    </div>
  );
};
