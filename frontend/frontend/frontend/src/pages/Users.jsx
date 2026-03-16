import React, { useState, useEffect, useCallback } from "react";
import { api, toArray, userPK } from "../api";
import {
  Topbar, Card, Table, Tr, Td, Btn, Modal,
  FormGroup, FormRow, Input, Select, RoleBadge,
} from "../components/UI";

// ── UserModal ─────────────────────────────────────────────────────────────────
// FIX: receives onSaved and uses it instead of calling load() directly
// FIX: removed reference to undefined editUser and load variables
function UserModal({ open, onClose, user, onSaved, toast }) {
  const isEdit = !!user;
  const [f, setF] = useState({ userName: "", email: "", password: "", role: "STUDENT" });
  const [saving, setSaving] = useState(false);
  const set = (k) => (e) => setF((p) => ({ ...p, [k]: e.target.value }));

  useEffect(() => {
    if (user) setF({ userName: user.userName || "", email: user.email || "", password: "", role: user.role || "STUDENT" });
    else      setF({ userName: "", email: "", password: "", role: "STUDENT" });
  }, [user, open]);

  const save = async () => {
    if (!f.userName || !f.email || (!isEdit && !f.password)) {
      toast("All fields required", "error"); return;
    }
    setSaving(true);
    try {
      const body = { userName: f.userName, email: f.email, role: f.role };
      if (f.password) body.password = f.password;

      // FIX: use user.id from the prop, not undefined editUser
      if (isEdit) await api.updateUser(userPK(user), body);
      else        await api.registerUser({ ...body, password: f.password });

      toast(isEdit ? "User updated!" : "User registered!", "success");
      onClose();

      // FIX: call onSaved() instead of load() — onSaved is load() passed from parent
      // 400ms delay lets backend cache evict before re-fetch
      setTimeout(() => onSaved(), 400);

    } catch (e) {
      toast("Error: " + e.message, "error");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={isEdit ? "Edit User" : "Register New User"}
      footer={
        <>
          <Btn variant="ghost" onClick={onClose}>Cancel</Btn>
          <Btn variant="primary" onClick={save} disabled={saving}>
            {saving ? "Saving…" : isEdit ? "Save Changes" : "Register"}
          </Btn>
        </>
      }
    >
      <FormRow>
        <FormGroup label="Username">
          <Input value={f.userName} onChange={set("userName")} placeholder="johndoe" />
        </FormGroup>
        <FormGroup label="Email">
          <Input type="email" value={f.email} onChange={set("email")} placeholder="john@example.com" />
        </FormGroup>
        <FormGroup label={isEdit ? "Password (blank = keep)" : "Password"}>
          <Input type="password" value={f.password} onChange={set("password")} placeholder="••••••••" />
        </FormGroup>
        <FormGroup label="Role">
          <Select value={f.role} onChange={set("role")}>
            <option value="STUDENT">STUDENT</option>
            <option value="LIBRARIAN">LIBRARIAN</option>
            <option value="FACULTY">FACULTY</option>
            <option value="STAFF">STAFF</option>
          </Select>
        </FormGroup>
      </FormRow>
    </Modal>
  );
}

// ── Users Page ────────────────────────────────────────────────────────────────
export default function Users({ toast }) {
  const [users,   setUsers]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal,   setModal]   = useState(false);
  const [editUser, setEdit]   = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setUsers(toArray(await api.getAllUsers()));
    } catch (e) {
      toast("Failed to load users: " + e.message, "error");
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { load(); }, [load]);

  // FIX: optimistic delete + delay before re-fetch
  const del = async (id) => {
    if (!window.confirm(`Delete user #${id}?`)) return;
    try {
      await api.deleteUser(id);
      // Remove from state instantly so UI updates right away
      setUsers((prev) => prev.filter((u) => userPK(u) !== id));
      toast("User deleted", "success");
      // Re-fetch after delay to confirm DB state
      setTimeout(() => load(), 400);
    } catch (e) {
      toast("Error: " + e.message, "error");
    }
  };

  return (
    <div className="page-animate">
      <Topbar title="User" accent="Registry">
        <Btn variant="ghost" size="sm" onClick={load}>⟳</Btn>
        <Btn variant="primary" onClick={() => { setEdit(null); setModal(true); }}>
          + Register User
        </Btn>
      </Topbar>

      <div style={{ padding: "28px 32px" }}>
        <Card>
          <Table
            headers={["ID", "Username", "Email", "Role", "Actions"]}
            loading={loading}
            empty="No users registered"
          >
            {users.map((u, i) => {
              const uid = userPK(u);
              return (
                <Tr key={uid ?? i}>
                  <Td muted>{String(uid ?? i + 1)}</Td>
                  <Td><strong>{String(u.userName ?? "—")}</strong></Td>
                  <Td>{String(u.email ?? "—")}</Td>
                  <Td><RoleBadge role={u.role} /></Td>
                  <Td>
                    <div style={{ display: "flex", gap: 5 }}>
                      <Btn variant="secondary" size="sm"
                        onClick={() => { setEdit(u); setModal(true); }}>
                        Edit
                      </Btn>
                      <Btn variant="danger" size="sm" onClick={() => del(uid)}>
                        Delete
                      </Btn>
                    </div>
                  </Td>
                </Tr>
              );
            })}
          </Table>
        </Card>
      </div>

      <UserModal
        open={modal}
        onClose={() => setModal(false)}
        user={editUser}
        onSaved={load}   
        toast={toast}
      />
    </div>
  );
}