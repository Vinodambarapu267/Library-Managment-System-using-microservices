import React from "react";

// ── Spinner ───────────────────────────────────────────────────────────────────
export function Spinner({ size = 20 }) {
  return (
    <div
      style={{
        display: "inline-block",
        width: size,
        height: size,
        flexShrink: 0,
        border: "1.5px solid var(--border2)",
        borderTopColor: "var(--gold)",
        borderRadius: "50%",
        animation: "spin .7s linear infinite",
      }}
    />
  );
}

// ── Button ────────────────────────────────────────────────────────────────────
const BV = {
  primary: {
    bg: "var(--gold)",
    color: "#0e0d0b",
    border: "none",
    hoverBg: "var(--gold-h)",
  },
  secondary: {
    bg: "var(--surface2)",
    color: "var(--text2)",
    border: "1px solid var(--border)",
    hoverBg: "var(--surface3)",
  },
  danger: {
    bg: "rgba(217,95,95,.12)",
    color: "var(--red)",
    border: "1px solid rgba(217,95,95,.2)",
    hoverBg: "rgba(217,95,95,.2)",
  },
  success: {
    bg: "rgba(61,184,160,.12)",
    color: "var(--teal)",
    border: "1px solid rgba(61,184,160,.2)",
    hoverBg: "rgba(61,184,160,.22)",
  },
  ghost: {
    bg: "transparent",
    color: "var(--muted)",
    border: "1px solid var(--border)",
    hoverBg: "var(--surface2)",
  },
};

export function Btn({
  variant = "secondary",
  size = "md",
  onClick,
  disabled,
  children,
  style = {},
}) {
  const v = BV[variant] || BV.secondary;
  const [hov, setHov] = React.useState(false);
  return (
    <button
      onClick={onClick}
      disabled={disabled}
      onMouseEnter={() => setHov(true)}
      onMouseLeave={() => setHov(false)}
      style={{
        padding: size === "sm" ? "5px 12px" : "8px 18px",
        borderRadius: size === "sm" ? 6 : 8,
        fontSize: size === "sm" ? 12 : 13,
        fontFamily: "var(--font-body)",
        fontWeight: 600,
        cursor: disabled ? "not-allowed" : "pointer",
        opacity: disabled ? 0.45 : 1,
        display: "inline-flex",
        alignItems: "center",
        gap: 6,
        whiteSpace: "nowrap",
        transition: "all .15s",
        background: hov && !disabled ? v.hoverBg : v.bg,
        color: v.color,
        border: v.border,
        transform:
          hov && !disabled && variant === "primary"
            ? "translateY(-1px)"
            : "none",
        boxShadow:
          hov && !disabled && variant === "primary"
            ? "0 4px 16px rgba(201,150,58,.25)"
            : "none",
        ...style,
      }}
    >
      {children}
    </button>
  );
}

// ── Badge ─────────────────────────────────────────────────────────────────────
const BCLR = {
  green: { bg: "rgba(61,184,160,.13)", color: "var(--teal)" },
  red: { bg: "rgba(217,95,95,.13)", color: "var(--red)" },
  blue: { bg: "rgba(91,145,212,.13)", color: "var(--blue)" },
  amber: { bg: "rgba(212,133,58,.13)", color: "var(--amber)" },
  gold: { bg: "rgba(201,150,58,.13)", color: "var(--gold)" },
  gray: { bg: "rgba(112,104,96,.18)", color: "var(--muted)" },
};

export function Badge({ color = "gray", children }) {
  const c = BCLR[color] || BCLR.gray;
  return (
    <span
      style={{
        display: "inline-flex",
        alignItems: "center",
        gap: 4,
        padding: "3px 8px",
        borderRadius: 20,
        fontSize: 11,
        fontWeight: 600,
        letterSpacing: ".3px",
        background: c.bg,
        color: c.color,
      }}
    >
      {children}
    </span>
  );
}

export function BookBadge({ status }) {
  return status === "AVAILABLE" ? (
    <Badge color="green">● AVAILABLE</Badge>
  ) : (
    <Badge color="red">● UNAVAILABLE</Badge>
  );
}
export function LoanBadge({ status }) {
  const m = { ACTIVE: "blue", OVERDUE: "red", RETURNED: "green" };
  return <Badge color={m[status] || "gray"}>{status || "—"}</Badge>;
}
export function RoleBadge({ role }) {
  return role === "LIBRARIAN" ? (
    <Badge color="gold">LIBRARIAN</Badge>
  ) : (
    <Badge color="blue">STUDENT</Badge>
  );
}
export function FineBadge({ status }) {
  return status === "PAID" ? (
    <Badge color="green">PAID</Badge>
  ) : (
    <Badge color="red">PENDING</Badge>
  );
}

// ── Table ─────────────────────────────────────────────────────────────────────
export function Table({
  headers = [],
  children,
  loading = false,
  empty = "No records found",
}) {
  const count = React.Children.count(children);
  return (
    <div style={{ overflowX: "auto" }}>
      <table
        style={{ width: "100%", borderCollapse: "collapse", fontSize: 13.5 }}
      >
        <thead>
          <tr style={{ borderBottom: "1px solid var(--border2)" }}>
            {headers.map((h) => (
              <th
                key={h}
                style={{
                  padding: "11px 16px",
                  textAlign: "left",
                  fontSize: 10.5,
                  fontWeight: 600,
                  letterSpacing: "1.2px",
                  textTransform: "uppercase",
                  color: "var(--muted)",
                  background: "rgba(255,245,220,.02)",
                  fontFamily: "var(--font-body)",
                }}
              >
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr>
              <td
                colSpan={headers.length}
                style={{ padding: 48, textAlign: "center" }}
              >
                <Spinner size={24} />
              </td>
            </tr>
          ) : count === 0 ? (
            <tr>
              <td
                colSpan={headers.length}
                style={{
                  padding: 52,
                  textAlign: "center",
                  color: "var(--muted)",
                  fontSize: 14,
                }}
              >
                <div style={{ fontSize: 36, marginBottom: 12, opacity: 0.3 }}>
                  📭
                </div>
                {empty}
              </td>
            </tr>
          ) : (
            children
          )}
        </tbody>
      </table>
    </div>
  );
}

export function Tr({ children, highlight }) {
  const [hov, setHov] = React.useState(false);
  return (
    <tr
      style={{
        background: highlight
          ? "rgba(217,95,95,.04)"
          : hov
            ? "rgba(255,245,220,.025)"
            : "transparent",
        transition: "background .12s",
      }}
      onMouseEnter={() => setHov(true)}
      onMouseLeave={() => setHov(false)}
    >
      {children}
    </tr>
  );
}

export function Td({ children, mono, muted }) {
  const safe = (v) => {
    if (v === null || v === undefined) return "—";
    if (React.isValidElement(v)) return v;
    if (typeof v === "object") return JSON.stringify(v);
    return v;
  };
  return (
    <td
      style={{
        padding: "12px 16px",
        borderBottom: "1px solid var(--border)",
        verticalAlign: "middle",
        fontFamily: mono ? "var(--font-mono)" : "var(--font-body)",
        fontSize: mono ? 11.5 : undefined,
        color: muted ? "var(--muted)" : undefined,
      }}
    >
      {typeof children === "object" && !React.isValidElement(children)
        ? safe(children)
        : children}
    </td>
  );
}

// ── Card ──────────────────────────────────────────────────────────────────────
export function Card({ children, style = {} }) {
  return (
    <div
      style={{
        background: "var(--surface)",
        border: "1px solid var(--border)",
        borderRadius: 14,
        overflow: "hidden",
        boxShadow: "var(--shadow)",
        ...style,
      }}
    >
      {children}
    </div>
  );
}

export function CardHeader({ title, subtitle, actions }) {
  return (
    <div
      style={{
        padding: "16px 22px",
        borderBottom: "1px solid var(--border)",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
      }}
    >
      <div>
        <div
          style={{
            fontFamily: "var(--font-display)",
            fontSize: 18,
            fontWeight: 700,
          }}
        >
          {title}
        </div>
        {subtitle && (
          <div style={{ fontSize: 12, color: "var(--muted)", marginTop: 2 }}>
            {subtitle}
          </div>
        )}
      </div>
      {actions && <div style={{ display: "flex", gap: 8 }}>{actions}</div>}
    </div>
  );
}

// ── Modal ─────────────────────────────────────────────────────────────────────
export function Modal({ open, onClose, title, children, footer }) {
  if (!open) return null;
  return (
    <div
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
      style={{
        marginTop: "20vh",
        position: "absolute",
        inset: 0,
        background: "rgba(0,0,0,.75)",
        backdropFilter: "blur(6px)",
        zIndex: 200,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        animation: "fadeIn .2s ease",
      }}
    >
      <div
        style={{
          background: "var(--surface)",
          border: "1px solid var(--border2)",
          borderRadius: 18,
          width: 500,
          maxWidth: "95vw",
          maxHeight: "92vh",
          overflowY: "auto",
          boxShadow: "var(--shadow-lg)",
          animation: "fadeUp .2s ease",
        }}
      >
        <div
          style={{
            padding: "20px 24px 16px",
            borderBottom: "1px solid var(--border)",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
          }}
        >
          <span
            style={{
              fontFamily: "var(--font-display)",
              fontSize: 20,
              fontWeight: 700,
            }}
          >
            {title}
          </span>
          <button
            onClick={onClose}
            style={{
              width: 30,
              height: 30,
              borderRadius: 7,
              border: "none",
              background: "var(--surface2)",
              color: "var(--muted)",
              cursor: "pointer",
              fontSize: 16,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              transition: "all .15s",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = "var(--red)";
              e.currentTarget.style.color = "#fff";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = "var(--surface2)";
              e.currentTarget.style.color = "var(--muted)";
            }}
          >
            ✕
          </button>
        </div>
        <div style={{ padding: "20px 24px" }}>{children}</div>
        {footer && (
          <div
            style={{
              padding: "12px 24px",
              borderTop: "1px solid var(--border)",
              display: "flex",
              gap: 10,
              justifyContent: "flex-end",
            }}
          >
            {footer}
          </div>
        )}
      </div>
    </div>
  );
}

// ── Form ──────────────────────────────────────────────────────────────────────
export function FormRow({ children }) {
  return (
    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
      {children}
    </div>
  );
}

export function FormGroup({ label, children, span }) {
  return (
    <div style={{ marginBottom: 14, gridColumn: span ? "1/-1" : undefined }}>
      <label
        style={{
          display: "block",
          fontSize: 11,
          fontWeight: 600,
          color: "var(--muted)",
          marginBottom: 6,
          letterSpacing: ".6px",
          textTransform: "uppercase",
          fontFamily: "var(--font-body)",
        }}
      >
        {label}
      </label>
      {children}
    </div>
  );
}

const IS = {
  width: "100%",
  padding: "9px 12px",
  borderRadius: 8,
  background: "var(--surface2)",
  border: "1px solid var(--border)",
  color: "var(--text)",
  fontFamily: "var(--font-body)",
  fontSize: 14,
  outline: "none",
  transition: "border-color .15s, box-shadow .15s",
};

export function Input({
  value,
  onChange,
  placeholder,
  type = "text",
  readOnly,
  onKeyDown,
}) {
  return (
    <input
      type={type}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      readOnly={readOnly}
      onKeyDown={onKeyDown}
      onFocus={(e) => {
        e.target.style.borderColor = "var(--gold)";
        e.target.style.boxShadow = "0 0 0 3px rgba(201,150,58,.1)";
      }}
      onBlur={(e) => {
        e.target.style.borderColor = "var(--border)";
        e.target.style.boxShadow = "none";
      }}
      style={{ ...IS, opacity: readOnly ? 0.65 : 1 }}
    />
  );
}

export function Select({ value, onChange, children }) {
  return (
    <select
      value={value}
      onChange={onChange}
      onFocus={(e) => {
        e.target.style.borderColor = "var(--gold)";
      }}
      onBlur={(e) => {
        e.target.style.borderColor = "var(--border)";
      }}
      style={{ ...IS, cursor: "pointer" }}
    >
      {children}
    </select>
  );
}

export function SearchBar({ value, onChange, placeholder, onKeyDown }) {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        gap: 8,
        background: "var(--surface2)",
        border: "1px solid var(--border)",
        borderRadius: 8,
        padding: "7px 12px",
        maxWidth: 280,
      }}
    >
      <span style={{ color: "var(--muted)", fontSize: 13 }}>⌕</span>
      <input
        value={value}
        onChange={onChange}
        onKeyDown={onKeyDown}
        placeholder={placeholder}
        style={{
          background: "none",
          border: "none",
          outline: "none",
          color: "var(--text)",
          fontSize: 13.5,
          flex: 1,
          fontFamily: "var(--font-body)",
        }}
      />
    </div>
  );
}

// ── Topbar ────────────────────────────────────────────────────────────────────
export function Topbar({ title, accent, children }) {
  return (
    <div
      style={{
        padding: "18px 32px",
        borderBottom: "1px solid var(--border)",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        background: "rgba(14,13,11,.7)",
        backdropFilter: "blur(12px)",
        position: "sticky",
        top: 0,
        zIndex: 50,
      }}
    >
      <div
        style={{
          fontFamily: "var(--font-display)",
          fontSize: 24,
          fontWeight: 700,
          letterSpacing: "-.3px",
        }}
      >
        {title} <span style={{ color: "var(--gold)" }}>{accent}</span>
      </div>
      <div
        style={{
          display: "flex",
          gap: 8,
          alignItems: "center",
          flexWrap: "wrap",
        }}
      >
        {children}
      </div>
    </div>
  );
}

// ── StatCard ──────────────────────────────────────────────────────────────────
export function StatCard({ icon, iconBg, value, label, trend }) {
  const [hov, setHov] = React.useState(false);
  return (
    <div
      onMouseEnter={() => setHov(true)}
      onMouseLeave={() => setHov(false)}
      style={{
        background: hov ? "var(--surface2)" : "var(--surface)",
        border: `1px solid ${hov ? "var(--border2)" : "var(--border)"}`,
        borderRadius: 14,
        padding: "18px 20px",
        display: "flex",
        alignItems: "center",
        gap: 16,
        transition: "all .2s",
        cursor: "default",
        transform: hov ? "translateY(-2px)" : "none",
        boxShadow: hov ? "var(--shadow)" : "none",
      }}
    >
      <div
        style={{
          fontSize: 26,
          width: 50,
          height: 50,
          borderRadius: 12,
          background: iconBg || "var(--gold-dim)",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          flexShrink: 0,
        }}
      >
        {icon}
      </div>
      <div>
        <div
          style={{
            fontFamily: "var(--font-display)",
            fontSize: 32,
            fontWeight: 700,
            lineHeight: 1,
            color: "var(--text)",
          }}
        >
          {value}
        </div>
        <div
          style={{
            fontSize: 11,
            color: "var(--muted)",
            marginTop: 4,
            fontWeight: 600,
            textTransform: "uppercase",
            letterSpacing: "1px",
          }}
        >
          {label}
        </div>
      </div>
    </div>
  );
}

// ── Sidebar ───────────────────────────────────────────────────────────────────
const NAV = [
  { id: "dashboard", icon: "◈", label: "Dashboard" },
  { id: "books", icon: "⊟", label: "Books" },
  { id: "users", icon: "⊙", label: "Users" },
  { id: "loans", icon: "⇄", label: "Loans" },
  { id: "fines", icon: "⊘", label: "Fines" },
];

export function Sidebar({ active, onNav }) {
  return (
    <aside
      style={{
        position: "fixed",
        left: 0,
        top: 0,
        bottom: 0,
        width: "var(--sw)",
        background: "var(--bg2)",
        borderRight: "1px solid var(--border)",
        display: "flex",
        flexDirection: "column",
        zIndex: 100,
      }}
    >
      {/* Logo */}
      <div
        style={{
          padding: "28px 24px 22px",
          borderBottom: "1px solid var(--border)",
        }}
      >
        <div
          style={{
            fontFamily: "var(--font-display)",
            fontSize: 26,
            fontWeight: 700,
            color: "var(--gold)",
            letterSpacing: "-.5px",
          }}
        >
          LibraMS
        </div>
        <div
          style={{
            fontSize: 10,
            color: "var(--muted)",
            letterSpacing: "2px",
            textTransform: "uppercase",
            marginTop: 4,
          }}
        >
          Library Management
        </div>
      </div>

      {/* Nav */}
      <nav
        style={{
          flex: 1,
          padding: "18px 12px",
          display: "flex",
          flexDirection: "column",
          gap: 2,
        }}
      >
        {NAV.map((n) => {
          const isActive = active === n.id;
          return (
            <button
              key={n.id}
              onClick={() => onNav(n.id)}
              style={{
                display: "flex",
                alignItems: "center",
                gap: 11,
                padding: "9px 13px",
                borderRadius: 9,
                border: isActive
                  ? "1px solid rgba(201,150,58,.2)"
                  : "1px solid transparent",
                background: isActive ? "var(--gold-dim)" : "transparent",
                color: isActive ? "var(--gold)" : "var(--muted)",
                fontSize: 13.5,
                fontWeight: isActive ? 600 : 500,
                cursor: "pointer",
                fontFamily: "var(--font-body)",
                transition: "all .15s",
                width: "100%",
                textAlign: "left",
              }}
              onMouseEnter={(e) => {
                if (!isActive) {
                  e.currentTarget.style.background = "var(--surface)";
                  e.currentTarget.style.color = "var(--text2)";
                }
              }}
              onMouseLeave={(e) => {
                if (!isActive) {
                  e.currentTarget.style.background = "transparent";
                  e.currentTarget.style.color = "var(--muted)";
                }
              }}
            >
              <span
                style={{
                  fontSize: 16,
                  width: 20,
                  textAlign: "center",
                  flexShrink: 0,
                }}
              >
                {n.icon}
              </span>
              <span>{n.label}</span>
              {isActive && (
                <span
                  style={{
                    marginLeft: "auto",
                    width: 5,
                    height: 5,
                    borderRadius: "50%",
                    background: "var(--gold)",
                  }}
                />
              )}
            </button>
          );
        })}
      </nav>

      {/* Footer */}
      <div style={{ padding: "0 14px 20px" }}>
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: 8,
            background: "var(--surface)",
            border: "1px solid var(--border)",
            borderRadius: 8,
            padding: "8px 11px",
            marginBottom: 6,
          }}
        >
          <div
            style={{
              width: 6,
              height: 6,
              borderRadius: "50%",
              background: "var(--teal)",
              animation: "pulse 2s infinite",
              flexShrink: 0,
            }}
          />
          <span
            style={{
              fontFamily: "var(--font-mono)",
              fontSize: 10.5,
              color: "var(--muted)",
            }}
          >
            localhost:9095
          </span>
        </div>
        <div
          style={{
            fontSize: 9.5,
            color: "var(--muted)",
            paddingLeft: 4,
            letterSpacing: ".5px",
          }}
        >
          Spring Boot · Spring Cloud
        </div>
      </div>
    </aside>
  );
}

// ── Toast Container ───────────────────────────────────────────────────────────
const TC = { success: "var(--teal)", error: "var(--red)", info: "var(--blue)" };
const TI = { success: "✓", error: "✕", info: "i" };

export function ToastContainer({ toasts, onRemove }) {
  return (
    <div
      style={{
        position: "fixed",
        top: 20,
        right: 20,
        zIndex: 999,
        display: "flex",
        flexDirection: "column",
        gap: 8,
        pointerEvents: "none",
      }}
    >
      {toasts.map((t) => (
        <div
          key={t.id}
          onClick={() => onRemove(t.id)}
          style={{
            background: "var(--surface)",
            border: "1px solid var(--border2)",
            borderLeft: `3px solid ${TC[t.type] || TC.info}`,
            borderRadius: 10,
            padding: "11px 16px",
            fontSize: 13.5,
            fontWeight: 500,
            boxShadow: "var(--shadow)",
            display: "flex",
            alignItems: "center",
            gap: 10,
            animation: "slideIn .3s ease",
            pointerEvents: "auto",
            maxWidth: 340,
            cursor: "pointer",
            color: "var(--text)",
            fontFamily: "var(--font-body)",
          }}
        >
          <span
            style={{
              width: 20,
              height: 20,
              borderRadius: "50%",
              background: TC[t.type],
              color: "#fff",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              fontSize: 11,
              fontWeight: 700,
              flexShrink: 0,
            }}
          >
            {TI[t.type]}
          </span>
          <span>{t.msg}</span>
        </div>
      ))}
    </div>
  );
}
