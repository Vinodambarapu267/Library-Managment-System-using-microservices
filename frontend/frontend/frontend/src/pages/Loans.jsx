import React, { useState, useEffect, useCallback, useRef } from "react";
import { api, toArray, userPK, normalizeLoan } from "../api";
import {
  Topbar, Card, Table, Tr, Td, Btn, Modal,
  FormGroup, Input, SearchBar, LoanBadge,
} from "../components/UI";

// ── BorrowModal ───────────────────────────────────────────────────────────────
function BorrowModal({ open, onClose, onSaved, toast }) {
  const [f, setF]           = useState({ title: "", userId: "" });
  const [saving, setSaving] = useState(false);
  const set = (k) => (e) => setF((p) => ({ ...p, [k]: e.target.value }));

  const save = async () => {
    if (!f.title.trim() || !f.userId) {
      toast("Book title and User ID required", "error"); return;
    }
    setSaving(true);
    try {
      await api.borrowBook({ title: f.title.trim(), userId: parseInt(f.userId) });
      toast("Book borrowed successfully!", "success");
      setF({ title: "", userId: "" });
      onClose();
      // FIX — delay before reload so backend cache evicts first
      setTimeout(() => onSaved(), 500);
    } catch (e) {
      toast("Error: " + e.message, "error");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal open={open} onClose={onClose} title="Borrow a Book"
      footer={<>
        <Btn variant="ghost" onClick={onClose}>Cancel</Btn>
        <Btn variant="primary" onClick={save} disabled={saving}>
          {saving ? "Borrowing…" : "Borrow"}
        </Btn>
      </>}
    >
      <div style={{
        background: "var(--surface2)", border: "1px solid var(--border)",
        borderRadius: 8, padding: "12px 14px", marginBottom: 16,
        fontSize: 12, color: "var(--muted)",
      }}>
        ⚠ Loan Service validates book availability and user status
        via Feign clients before creating the loan.
      </div>
      <FormGroup label="Book Title — must match exactly (case-sensitive)">
        <Input value={f.title} onChange={set("title")}
          placeholder="e.g. Spring Microservices in Action" />
      </FormGroup>
      <FormGroup label="User ID">
        <Input type="number" value={f.userId} onChange={set("userId")}
          placeholder="Registered user ID" />
      </FormGroup>
    </Modal>
  );
}

// ── Loans Page ────────────────────────────────────────────────────────────────
export default function Loans({ toast }) {
  const [loans,      setLoans]   = useState([]);
  const [rawAll,     setRawAll]  = useState([]);
  const [loading,    setLoading] = useState(true);
  const [search,     setSearch]  = useState("");
  const [borrowOpen, setBorrow]  = useState(false);

  const loadAll = useCallback(async () => {
    setLoading(true);
    try {
      const raw     = await api.getTotalLoans();
      const arr     = toArray(raw);
      const allLoans = arr.map(normalizeLoan);
      setRawAll(allLoans);
      setLoans(allLoans);
    } catch (e) {
      toast("Failed to load loans: " + e.message, "error");
    } finally {
      setLoading(false);
    }
  }, [toast]);

  // FIX — removed loaded.current guard so loadAll can be called anytime
  useEffect(() => { loadAll(); }, [loadAll]);

const handleSearch = async () => {
  const uid = search.trim();

  // If search is empty — restore full list
  if (!uid) {
    setLoans(rawAll);
    return;
  }

  // Validate — must be a number
  if (isNaN(uid)) {
    toast("User ID must be a number", "error");
    return;
  }

  setLoading(true);
  try {
    const res  = await api.getLoansByUser(uid);
    const data = toArray(res).map(normalizeLoan);

    if (data.length === 0) {
      toast(`No loans found for User ID: ${uid}`, "info");
      setLoans([]);
    } else {
      setLoans(data);
      toast(`Found ${data.length} loan(s) for User ID: ${uid}`, "success");
    }
  } catch (e) {
    // Distinguish between "not found" and real errors
    if (e.message.includes("403") || e.message.includes("404") || e.message.includes("Not Found")) {
      toast(`No loans found for User ID: ${uid}`, "info");
    } else {
      toast("Search failed: " + e.message, "error");
    }
    setLoans([]);
  } finally {
    setLoading(false);
  }
};
  const handleReturn = async (loan) => {
    const id = loan.id ?? loan.loanId;
    if (!id)                        { toast("Loan ID is missing", "error"); return; }
    if (loan.status === "RETURNED") { toast("Book already returned", "info"); return; }
    if (!window.confirm(`Return loan #${id}?`)) return;
    try {
      await api.returnBook(id);
      toast("Book returned successfully!", "success");
      setTimeout(() => loadAll(), 500);
    } catch (e) {
      toast("Error: " + e.message, "error");
    }
  };

  // FIX — count BORROWED as active too (your backend uses BORROWED not ACTIVE)
  const active   = loans.filter((l) => l.status === "BORROWED" || l.status === "ACTIVE").length;
  const overdue  = loans.filter((l) => l.status === "OVERDUE").length;
  const returned = loans.filter((l) => l.status === "RETURNED").length;

  return (
    <div className="page-animate">
      <Topbar title="Loan" accent="Management">
        {overdue > 0 && (
          <span style={{
            fontSize: 12, color: "var(--red)",
            background: "rgba(217,95,95,.1)",
            border: "1px solid rgba(217,95,95,.2)",
            borderRadius: 6, padding: "4px 10px", fontWeight: 600,
          }}>⚠ {overdue} overdue</span>
        )}
        <SearchBar value={search} onChange={(e) => setSearch(e.target.value)}
          placeholder="Filter by User ID…"
          onKeyDown={(e) => e.key === "Enter" && handleSearch()} />
        <Btn variant="secondary" size="sm" onClick={handleSearch}>Filter</Btn>
        <Btn variant="ghost"     size="sm" onClick={loadAll}>⟳ All</Btn>
        <Btn variant="primary" onClick={() => setBorrow(true)}>+ Borrow Book</Btn>
      </Topbar>

      <div style={{ padding: "28px 32px" }}>
        {!loading && rawAll.length > 0 && (
          <div style={{
            display: "flex", gap: 16, marginBottom: 20,
            fontSize: 12, color: "var(--muted)",
            fontFamily: "var(--font-mono)",
          }}>
            <span>Total: <strong style={{ color: "var(--text)" }}>{loans.length}</strong></span>
            <span>Active: <strong style={{ color: "var(--blue)" }}>{active}</strong></span>
            <span>Overdue: <strong style={{ color: "var(--red)" }}>{overdue}</strong></span>
            <span>Returned: <strong style={{ color: "var(--teal)" }}>{returned}</strong></span>
          </div>
        )}

        <Card>
          <Table
            headers={["Loan ID","Book Title","User ID","Borrowed","Due Date","Returned","Amount","Status","Actions"]}
            loading={loading}
            empty="No loans found"
          >
            {loans.map((l, i) => (
              <Tr key={l.id ?? i} highlight={l.status === "OVERDUE"}>
                <Td mono>{String(l.id ?? i + 1)}</Td>
                <Td><strong>{l.title  || "—"}</strong></Td>
                <Td muted>{l.userId   || "—"}</Td>
                <Td muted>{l.borrowedAt || "—"}</Td>
                <Td muted>{l.dueDate  || "—"}</Td>
                <Td muted>{l.returnedAt || "—"}</Td>
                <Td>{l.totalAmount != null ? `₹${parseFloat(l.totalAmount).toFixed(2)}` : "—"}</Td>
                <Td><LoanBadge status={l.status} /></Td>
                <Td>{l.status !== "RETURNED"
                  ? <Btn variant="success" size="sm" onClick={() => handleReturn(l)}>↩ Return</Btn>
                  : <span style={{ color: "var(--muted)", fontSize: 12 }}>—</span>
                }</Td>
              </Tr>
            ))}
          </Table>
        </Card>
      </div>

      <BorrowModal
        open={borrowOpen}
        onClose={() => setBorrow(false)}
        onSaved={loadAll}
        toast={toast}
      />
    </div>
  );
}