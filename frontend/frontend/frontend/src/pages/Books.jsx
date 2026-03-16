import React, { useState, useEffect, useCallback } from "react";
import { api, toArray } from "../api";
import {
  Topbar,
  Card,
  Table,
  Tr,
  Td,
  Btn,
  Modal,
  Badge,
  FormGroup,
  FormRow,
  Input,
  SearchBar,
  BookBadge,
} from "../components/UI";

function AddBookModal({ open, onClose, onSaved, toast }) {
  const init = {
    isbn: "",
    title: "",
    author: "",
    category: "",
    totalCopies: 1,
    publishedYear: "",
  };
  const [f, setF] = useState(init);
  const [saving, setSaving] = useState(false);
  const set = (k) => (e) => setF((p) => ({ ...p, [k]: e.target.value }));

  const save = async () => {
    setSaving(true);
    try {
      await api.addBook({
        title: f.title,
        author: f.author,
        category: f.category,
        totalCopies: parseInt(f.totalCopies) || 1,
        publishedYear: parseInt(f.publishedYear) || 0,
        bookStatus: "AVAILABLE",
      });
      toast("Book added successfully!", "success");
      setF(init);
      onSaved();
      onClose();
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
      title="Add New Book"
      footer={
        <>
          <Btn variant="ghost" onClick={onClose}>
            Cancel
          </Btn>
          <Btn variant="primary" onClick={save} disabled={saving}>
            {saving ? "Adding…" : "Add Book"}
          </Btn>
        </>
      }
    >
      <FormRow>
        <FormGroup label="Title">
          <Input
            value={f.title}
            onChange={set("title")}
            placeholder="Book title"
          />
        </FormGroup>
        <FormGroup label="Author">
          <Input
            value={f.author}
            onChange={set("author")}
            placeholder="Author name"
          />
        </FormGroup>
        <FormGroup label="Category">
          <Input
            value={f.category}
            onChange={set("category")}
            placeholder="Genre / topic"
          />
        </FormGroup>
        <FormGroup label="Total Copies">
          <Input
            type="number"
            value={f.totalCopies}
            onChange={set("totalCopies")}
          />
        </FormGroup>
        <FormGroup label="Published Year">
          <Input
            type="number"
            value={f.publishedYear}
            onChange={set("publishedYear")}
            placeholder="2024"
          />
        </FormGroup>
      </FormRow>
    </Modal>
  );
}

function UpdateCopiesModal({ open, onClose, book, onSaved, toast }) {
  const [copies, setCopies] = useState(1);
  const [saving, setSaving] = useState(false);

  const save = async () => {
    setSaving(true);
    try {
      await api.updateCopies(book?.title, copies);
      toast("Copies updated!", "success");
      onSaved();
      onClose();
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
      title="Update Copies"
      footer={
        <>
          <Btn variant="ghost" onClick={onClose}>
            Cancel
          </Btn>
          <Btn variant="primary" onClick={save} disabled={saving}>
            {saving ? "Updating…" : "Update"}
          </Btn>
        </>
      }
    >
      <FormGroup label="Book Title">
        <Input value={book?.title || ""} readOnly />
      </FormGroup>
      <FormGroup label="New Total Copies">
        <Input
          type="number"
          value={copies}
          onChange={(e) => setCopies(e.target.value)}
        />
      </FormGroup>
    </Modal>
  );
}

export default function Books({ toast }) {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [addOpen, setAddOpen] = useState(false);
  const [copyBook, setCopyBook] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setBooks(toArray(await api.getAllBooks()));
    } catch (e) {
      toast("Failed to load books: " + e.message, "error");
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => {
    load();
  }, [load]);

  const handleSearch = async () => {
    if (!search.trim()) {
      load();
      return;
    }
    setLoading(true);
    try {
      setBooks(toArray(await api.findByAuthor(search.trim())));
    } catch (e) {
      setBooks([]);
      toast("No books found for: " + search, "info");
    } finally {
      setLoading(false);
    }
  };

  const del = async (isbn) => {
    if (!window.confirm(`Delete book ISBN "${isbn}"?`)) return;
    try {
      await api.deleteBook(isbn);
      toast("Book deleted", "success");
      // Remove instantly from state — don't wait for re-fetch
      setBooks((prev) => prev.filter((b) => b.isbn !== isbn));
      // Also re-fetch in background to sync with server
      load();
    } catch (e) {
      toast("Error: " + e.message, "error");
    }
  };
  return (
    <div className="page-animate">
      <Topbar title="Book" accent="Catalog">
        <SearchBar
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by author…"
          onKeyDown={(e) => e.key === "Enter" && handleSearch()}
        />
        <Btn variant="secondary" size="sm" onClick={handleSearch}>
          Search
        </Btn>
        <Btn variant="ghost" size="sm" onClick={load}>
          ⟳
        </Btn>
        <Btn variant="primary" onClick={() => setAddOpen(true)}>
          + Add Book
        </Btn>
      </Topbar>

      <div style={{ padding: "28px 32px" }}>
        <Card>
          <Table
            headers={[
              "ID",
              "ISBN",
              "Title",
              "Author",
              "Category",
              "Available",
              "Total",
              "Year",
              "Status",
              "Actions",
            ]}
            loading={loading}
            empty="No books in catalog"
          >
            {books.map((b, i) => (
              <Tr key={b.id ?? i}>
                <Td muted>{String(b.id ?? i + 1)}</Td>
                <Td mono>{String(b.isbn ?? "—")}</Td>
                <Td>
                  <strong>{String(b.title ?? "—")}</strong>
                </Td>
                <Td>{String(b.author ?? "—")}</Td>
                <Td>
                  <Badge color="gray">{String(b.category ?? "—")}</Badge>
                </Td>
                <Td>{String(b.copiesAvailable ?? 0)}</Td>
                <Td>{String(b.totalCopies ?? 0)}</Td>
                <Td muted>{String(b.publishedYear ?? "—")}</Td>
                <Td>
                  <BookBadge status={b.bookStatus} />
                </Td>
                <Td>
                  <div style={{ display: "flex", gap: 5 }}>
                    <Btn
                      variant="secondary"
                      size="sm"
                      onClick={() => setCopyBook(b)}
                    >
                      Copies
                    </Btn>
                    <Btn variant="danger" size="sm" onClick={() => del(b.isbn)}>
                      Delete
                    </Btn>
                  </div>
                </Td>
              </Tr>
            ))}
          </Table>
        </Card>
      </div>

      <AddBookModal
        open={addOpen}
        onClose={() => setAddOpen(false)}
        onSaved={load}
        toast={toast}
      />
      <UpdateCopiesModal
        open={!!copyBook}
        onClose={() => setCopyBook(null)}
        book={copyBook}
        onSaved={load}
        toast={toast}
      />
    </div>
  );
}
