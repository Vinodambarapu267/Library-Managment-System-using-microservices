import React, { useState, useEffect, useCallback } from "react";
import { api, toArray } from "../api";
import {
  Topbar,
  StatCard,
  Card,
  CardHeader,
  Table,
  Tr,
  Td,
  Btn,
  Spinner,
  BookBadge,
  FineBadge,
} from "../components/UI";

export default function Dashboard({ onNav, toast }) {
  const [stats, setStats] = useState({
    books: "—",
    users: "—",
    loans: "—",
    fines: "—",
  });
  const [books, setBooks] = useState([]);
  const [fines, setFines] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    const [rB, rU, rL, rF] = await Promise.allSettled([
      api.getAllBooks(),
      api.getAllUsers(),
      api.getTotalLoans(),
      api.getPendingFines(),
    ]);
    const bArr = rB.status === "fulfilled" ? toArray(rB.value) : [];
    const uArr = rU.status === "fulfilled" ? toArray(rU.value) : [];
    const fArr = rF.status === "fulfilled" ? toArray(rF.value) : [];

    setStats({
      books: bArr.length,
      users: uArr.length,
      loans:
        rL.status === "fulfilled"
          ? typeof rL.value === "number"
            ? rL.value
            : toArray(rL.value).length
          : "—",
      fines: fArr.length,
    });
    setBooks(bArr.slice(0, 6));
    setFines(fArr.slice(0, 5));
    setLoading(false);
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <div className="page-animate">
      <Topbar title="Dashboard" accent="Overview">
        <Btn variant="ghost" size="sm" onClick={load}>
          ⟳ Refresh
        </Btn>
      </Topbar>

      <div style={{ padding: "28px 32px" }}>
        {/* Stats */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(4,1fr)",
            gap: 14,
            marginBottom: 28,
          }}
        >
          <StatCard
            icon="📚"
            iconBg="rgba(201,150,58,.1)"
            value={loading ? <Spinner size={18} /> : stats.books}
            label="Total Books"
          />
          <StatCard
            icon="👤"
            iconBg="rgba(91,145,212,.1)"
            value={loading ? <Spinner size={18} /> : stats.users}
            label="Registered Users"
          />
          <StatCard
            icon="🔄"
            iconBg="rgba(61,184,160,.1)"
            value={loading ? <Spinner size={18} /> : stats.loans}
            label="Total Loans"
          />
          <StatCard
            icon="💰"
            iconBg="rgba(217,95,95,.1)"
            value={loading ? <Spinner size={18} /> : stats.fines}
            label="Pending Fines"
          />
        </div>

       
        {/* Recent tables */}
        <div
          style={{ display: "grid", gridTemplateColumns: "3fr 2fr", gap: 20 }}
        >
          <Card>
            <CardHeader
              title="Recent Books"
              actions={
                <Btn variant="ghost" size="sm" onClick={() => onNav("books")}>
                  View All →
                </Btn>
              }
            />
            <Table
              headers={["Title", "Author", "Copies", "Status"]}
              loading={loading}
              empty="No books yet"
            >
              {books.map((b, i) => (
                <Tr key={b.id ?? i}>
                  <Td>
                    <strong>{String(b.title ?? "—")}</strong>
                  </Td>
                  <Td muted>{String(b.author ?? "—")}</Td>
                  <Td>
                    {String(b.copiesAvailable ?? 0)}/
                    {String(b.totalCopies ?? 0)}
                  </Td>
                  <Td>
                    <BookBadge status={b.bookStatus} />
                  </Td>
                </Tr>
              ))}
            </Table>
          </Card>

          <Card>
            <CardHeader
              title="Pending Fines"
              actions={
                <Btn variant="ghost" size="sm" onClick={() => onNav("fines")}>
                  View All →
                </Btn>
              }
            />
            <Table
              headers={["Loan", "Amount", "Status"]}
              loading={loading}
              empty="No fines"
            >
              {fines.map((f, i) => (
                <Tr key={f.loanId ?? i}>
                  <Td mono>{String(f.loanId ?? "—")}</Td>
                  <Td>
                    <strong>₹{parseFloat(f.amount || 0).toFixed(2)}</strong>
                  </Td>
                  <Td>
                    <FineBadge status={f.status} />
                  </Td>
                </Tr>
              ))}
            </Table>
          </Card>
        </div>
      </div>
    </div>
  );
}
