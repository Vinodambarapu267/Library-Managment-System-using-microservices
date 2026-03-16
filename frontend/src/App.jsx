import React, { useState, useCallback } from 'react';
import { Sidebar, ToastContainer } from './components/UI';
import { useToast } from './hooks/useToast';
import Dashboard from './pages/Dashboard';
import Books     from './pages/Books';
import Users     from './pages/Users';
import Loans     from './pages/Loans';
import Fines     from './pages/Fines';

export default function App() {
  const [page, setPage] = useState('dashboard');
  const { toasts, toast, remove } = useToast();
  const nav = useCallback(p => setPage(p), []);

  const pages = {
    dashboard: <Dashboard onNav={nav} toast={toast} />,
    books:     <Books     toast={toast} />,
    users:     <Users     toast={toast} />,
    loans:     <Loans     toast={toast} />,
    fines:     <Fines     toast={toast} />,
  };

  return (
    <div style={{ position: 'relative', zIndex: 1 }}>
      <Sidebar active={page} onNav={nav} />
      <main style={{ marginLeft: 'var(--sw)', minHeight: '100vh' }}>
        {pages[page] ?? pages.dashboard}
      </main>
      <ToastContainer toasts={toasts} onRemove={remove} />
    </div>
  );
}
