import { useNavigate, useLocation } from 'react-router-dom';
import { BookOpen, Users, ArrowLeftRight, DollarSign, LayoutDashboard, X } from 'lucide-react';

const NAV = [
  { icon: LayoutDashboard, label: 'Dashboard', path: '/' },
  { section: 'Catalog' },
  { icon: BookOpen, label: 'Books', path: '/books' },
  { section: 'Members' },
  { icon: Users, label: 'Users', path: '/users' },
  { section: 'Transactions' },
  { icon: ArrowLeftRight, label: 'Loans', path: '/loans' },
  { icon: DollarSign, label: 'Fines', path: '/fines' },
];

export default function Sidebar({ open, onClose }) {
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const handleNav = (path) => {
    navigate(path);
    onClose();
  };

  return (
    <>
      <div className={`sidebar-overlay ${open ? 'open' : ''}`} onClick={onClose} />
      <aside className={`sidebar ${open ? 'open' : ''}`}>
        <div className="sidebar-logo">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <h1>Libra<br />MS</h1>
            <button
              className="btn btn-icon btn-ghost"
              onClick={onClose}
              style={{ display: 'none' }}
              id="sidebar-close-btn"
            >
              <X size={16} />
            </button>
          </div>
          <span>Library Management</span>
        </div>
        <nav className="sidebar-nav">
          {NAV.map((item, i) => {
            if (item.section) return <div key={i} className="nav-section-label">{item.section}</div>;
            const Icon = item.icon;
            const active = pathname === item.path;
            return (
              <div
                key={item.path}
                className={`nav-item ${active ? 'active' : ''}`}
                onClick={() => handleNav(item.path)}
              >
                <Icon size={16} />
                {item.label}
              </div>
            );
          })}
        </nav>
        <div className="sidebar-footer">
          <span className="dot" />
          API Gateway :9095
        </div>
      </aside>
    </>
  );
}
