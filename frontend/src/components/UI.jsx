import { X, AlertCircle, CheckCircle, Info } from 'lucide-react';

export function Spinner() {
  return <div className="spinner" />;
}

export function LoadingWrap() {
  return <div className="loading-wrap"><Spinner /></div>;
}

export function Alert({ type = 'info', children, onClose }) {
  const icons = { success: CheckCircle, error: AlertCircle, info: Info };
  const Icon = icons[type] || Info;
  return (
    <div className={`alert alert-${type}`}>
      <Icon size={16} style={{ flexShrink: 0, marginTop: 1 }} />
      <span style={{ flex: 1 }}>{children}</span>
      {onClose && (
        <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'inherit', padding: 0 }}>
          <X size={14} />
        </button>
      )}
    </div>
  );
}

export function Modal({ open, onClose, title, children, footer }) {
  if (!open) return null;
  return (
    <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <span className="modal-title">{title}</span>
          <button className="btn btn-icon btn-ghost" onClick={onClose}>
            <X size={16} />
          </button>
        </div>
        <div className="modal-body">{children}</div>
        {footer && <div className="modal-footer">{footer}</div>}
      </div>
    </div>
  );
}

export function Badge({ status }) {
  const map = {
    AVAILABLE: ['badge-green', 'Available'],
    UNAVAILABLE: ['badge-red', 'Unavailable'],
    ACTIVE: ['badge-blue', 'Active'],
    RETURNED: ['badge-green', 'Returned'],
    OVERDUE: ['badge-red', 'Overdue'],
    PAID: ['badge-green', 'Paid'],
    PENDING: ['badge-yellow', 'Pending'],
    STUDENT: ['badge-blue', 'Student'],
    LIBRARIAN: ['badge-yellow', 'Librarian'],
  };
  const [cls, label] = map[status] || ['badge-gray', status || '—'];
  return <span className={`badge ${cls}`}>{label}</span>;
}

export function ConfirmModal({ open, onClose, onConfirm, title, message, loading }) {
  return (
    <Modal
      open={open}
      onClose={onClose}
      title={title || 'Confirm'}
      footer={
        <>
          <button className="btn btn-ghost" onClick={onClose} disabled={loading}>Cancel</button>
          <button className="btn btn-danger" onClick={onConfirm} disabled={loading}>
            {loading ? <Spinner /> : 'Confirm'}
          </button>
        </>
      }
    >
      <p style={{ color: 'var(--text2)', fontSize: '0.9rem' }}>{message}</p>
    </Modal>
  );
}
