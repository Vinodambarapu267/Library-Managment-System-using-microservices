import React, { useState, useEffect, useCallback } from 'react';
import { api, toArray } from '../api';
import {
  Topbar, Card, Table, Tr, Td, Btn, Modal,
  FormGroup, Input, FineBadge,
} from '../components/UI';

function CreateFineModal({ open, onClose, onSaved, toast }) {
  const [loanId, setLoanId] = useState('');
  const [saving, setSaving] = useState(false);

  const save = async () => {
    if (!loanId) { toast('Loan ID required', 'error'); return; }
    setSaving(true);
    try {
      await api.createFine(loanId);
      toast('Fine created!', 'success');
      setLoanId(''); onSaved(); onClose();
    } catch(e) { toast('Error: ' + e.message, 'error'); }
    finally { setSaving(false); }
  };

  return (
    <Modal open={open} onClose={onClose} title="Create Fine"
      footer={<>
        <Btn variant="ghost" onClick={onClose}>Cancel</Btn>
        <Btn variant="danger" onClick={save} disabled={saving}>{saving ? 'Creating…' : 'Create Fine'}</Btn>
      </>}
    >
      <div style={{
        background: 'var(--surface2)', border: '1px solid var(--border)',
        borderRadius: 8, padding: '12px 14px', marginBottom: 16, fontSize: 12, color: 'var(--muted)',
      }}>
        ℹ The Fine Service also runs a daily @Scheduled job that auto-calculates fines
        for all overdue loans using: daysOverdue × dailyFineRate
      </div>
      <FormGroup label="Loan ID (must be an overdue loan)">
        <Input type="number" value={loanId} onChange={e => setLoanId(e.target.value)}
          placeholder="Enter loan ID"/>
      </FormGroup>
    </Modal>
  );
}

export default function Fines({ toast }) {
  const [fines,   setFines]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter,  setFilter]  = useState('all');
  const [createOpen, setCreate] = useState(false);

  const load = useCallback(async (type = filter) => {
    setLoading(true);
    try {
      const data = toArray(type === 'pending' ? await api.getPendingFines() : await api.getAllFines());
      setFines(data);
    } catch(e) { toast('Failed to load fines: ' + e.message, 'error'); }
    finally { setLoading(false); }
  }, [toast, filter]);

  useEffect(() => { load('all'); }, []); // eslint-disable-line

  const changeFilter = t => { setFilter(t); load(t); };

  const totalAmount = fines.reduce((s, f) => s + parseFloat(f.amount || 0), 0);
  const pending     = fines.filter(f => f.status === 'PENDING').length;

  return (
    <div className="page-animate">
      <Topbar title="Fine" accent="Tracker">
        <div style={{ display: 'flex', gap: 4 }}>
          {['all','pending'].map(t => (
            <Btn key={t} variant={filter === t ? 'primary' : 'ghost'} size="sm"
              onClick={() => changeFilter(t)}>
              {t === 'all' ? 'All Fines' : 'Pending Only'}
            </Btn>
          ))}
        </div>
        <Btn variant="ghost" size="sm" onClick={() => load()}>⟳</Btn>
        <Btn variant="primary" onClick={() => setCreate(true)}>+ Create Fine</Btn>
      </Topbar>

      <div style={{ padding: '28px 32px' }}>
        {/* Summary bar */}
        {!loading && fines.length > 0 && (
          <div style={{
            display: 'flex', gap: 24, marginBottom: 20,
            fontSize: 12, color: 'var(--muted)', fontFamily: 'var(--font-mono)',
          }}>
            <span>Total records: <strong style={{ color: 'var(--text)' }}>{fines.length}</strong></span>
            <span>Pending: <strong style={{ color: 'var(--red)' }}>{pending}</strong></span>
            <span>Total amount: <strong style={{ color: 'var(--gold)' }}>₹{totalAmount.toFixed(2)}</strong></span>
          </div>
        )}

        <Card>
          <Table
            headers={['Loan ID','Amount (₹)','Overdue Since','Status']}
            loading={loading} empty="No fines found"
          >
            {fines.map((f, i) => (
              <Tr key={f.loanId ?? i}>
                <Td mono>#{String(f.loanId ?? '—')}</Td>
                <Td><strong style={{ color: f.status === 'PENDING' ? 'var(--red)' : 'var(--teal)' }}>
                  ₹{parseFloat(f.amount || 0).toFixed(2)}
                </strong></Td>
                <Td muted>{String(f.overdueSince ?? '—')}</Td>
                <Td><FineBadge status={f.status}/></Td>
              </Tr>
            ))}
          </Table>
        </Card>
      </div>

      <CreateFineModal open={createOpen} onClose={() => setCreate(false)} onSaved={() => load()} toast={toast}/>
    </div>
  );
}
