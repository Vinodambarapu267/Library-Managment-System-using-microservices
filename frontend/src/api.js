const BASE = "";

// ── HTTP Request ──────────────────────────────────────────────────────────────
async function req(method, path, body) {
  const opts = {
    method,
    headers: {
      "Content-Type": "application/json",
      "Cache-Control": "no-cache, no-store",
      Pragma: "no-cache",
    },
  };
  if (body) opts.body = JSON.stringify(body);

  // Bust Spring + browser cache on every GET
  const url =
    method === "GET"
      ? `${BASE}${path}${path.includes("?") ? "&" : "?"}_t=${Date.now()}`
      : `${BASE}${path}`;

  const res = await fetch(url, opts);

  if (!res.ok) {
    const txt = await res.text().catch(() => `HTTP ${res.status}`);
    throw new Error(txt || `HTTP ${res.status}`);
  }

  const ct = res.headers.get("content-type") || "";
  if (!ct.includes("application/json")) return res.text();

  const json = await res.json();
  return unwrap(json);
}

// ── Response Unwrapper ────────────────────────────────────────────────────────

function unwrap(json) {
  if (json === null || json === undefined) return json;

  // Primitive — number, string, boolean (e.g. totalBooks count)
  if (typeof json !== "object") return json;

  if (Array.isArray(json)) return json;

  if ("data" in json && json.data !== null && json.data !== undefined) {
    return json.data;
  }

  if ("list" in json && json.list !== null && json.list !== undefined) {
    return json.list;
  }

  if ("users" in json && json.users !== null) return json.users;
  if ("books" in json && json.books !== null) return json.books;
  if ("loans" in json && json.loans !== null) return json.loans;
  if ("fines" in json && json.fines !== null) return json.fines;
  if ("result" in json && json.result !== null) return json.result;

  return json;
}

// ── toArray ───────────────────────────────────────────────────────────────────

export function toArray(val) {
  if (val === null || val === undefined) return [];
  if (Array.isArray(val)) return val;
  if (typeof val === "object") return [val]; // single object → wrap in array
  return [];
}

// ── Primary Key Helpers ───────────────────────────────────────────────────────
export const bookPK = (b) => b?.id ?? b?.bookId ?? null;
export const userPK = (u) => u?.id ?? u?.userId ?? null;
export const loanPK = (l) => l?.loanId ?? l?.id ?? null;

// ── Loan Field Normalizer ─────────────────────────────────────────────────────
export function normalizeLoan(l) {
  return {
    id:          l?.loanId      ?? l?.id          ?? null,
    title:       String(l?.title                  ?? "—"),
    userId:      String(l?.userId                 ?? "—"),
    borrowedAt:  String(l?.borrowAt               ?? l?.borrowedAt  ?? "—"),
    dueDate:     String(l?.dueDate                ?? "—"),
    returnedAt:  l?.returnedAt  ? String(l.returnedAt) : "—",
    totalAmount: l?.totalAmount ?? null,
    // Normalize status — map BORROWED → treat same as ACTIVE
    status:      String(l?.status ?? "—"),
  };
}

// ── API Calls ─────────────────────────────────────────────────────────────────
export const api = {
  // ── Book-Service  BASE: /api/books ──────────────────────────────────────────

  addBook: (body) => req("POST", "/api/books/addbook", body),
  updateBook: (id, body) => req("PUT", `/api/books/updatebook/${id}`, body),
  getBookByISBN: (isbn) => req("GET", `/api/books/getbyisbn?isbn=${isbn}`),
  getAllBooks: () => req("GET", "/api/books"),
  findByAuthor: (author) =>
    req("GET", `/api/books/findbyauthor/${encodeURIComponent(author)}`),
  getTotalBooks: () => req("GET", "/api/books/totalbooks"),
  deleteBook: (isbn) => req("DELETE", `/api/books/deletebook/${isbn}`),
  updateCopies: (title, n) =>
    req("PUT", `/api/books/${encodeURIComponent(title)}/copies/${n}`),
  issueBook: (title) =>
    req("PUT", `/api/books/${encodeURIComponent(title)}/issue`),
  returnBookCopy: (title) =>
    req("PUT", `/api/books/${encodeURIComponent(title)}/return`),
  checkAvailability: (title) =>
    req("GET", `/api/books/${encodeURIComponent(title)}/availability`),

  // ── User-Service  BASE: /api/users ──────────────────────────────────────────

  registerUser: (body) => req("POST", "/api/users/register", body),
  updateUser: (id, body) => req("PUT", `/api/users/updateUser/${id}`, body),
  getUserById: (id) => req("GET", `/api/users/getbyid/${id}`),
  getAllUsers: () => req("GET", "/api/users"),
  checkUserStatus: (id) => req("GET", `/api/users/${id}/checkstatus`),
  deleteUser: (id) => req("DELETE", `/api/users/deletebyid/${id}`),

  borrowBook: (body) => req("POST", "/api/loan/borrowbook", body),
  returnBook: (loanId) => req("PUT", `/api/loan/returnbook/${loanId}`),
  getLoansByUser: (userId) => req("GET", `/api/loan/${userId}/Loans`),
  getTotalLoans: () => req("GET", "/api/loan"),

  // ── Fine-Service  BASE: /api/fines ──────────────────────────────────────────

  createFine: (loanId) => req("POST", `/api/fines/createfine?loanId=${loanId}`),
  getAllFines: () => req("GET", "/api/fines"),
  getPendingFines: () => req("GET", "/api/fines/getallpendingfines"),
};
