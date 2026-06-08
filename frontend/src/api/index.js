import axios from "axios";

// Empty baseURL → requests go through Vite dev proxy (/api → localhost:9095)
// For production builds, set VITE_API_BASE in your .env file
const BASE = import.meta.env.VITE_API_BASE ?? "";

const api = axios.create({
  baseURL: BASE,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.response.use(
  (r) => r,
  (err) => {
    const msg =
      err.response?.data?.message ||
      (typeof err.response?.data === "string" ? err.response.data : null) ||
      err.message ||
      "Unknown error";
    return Promise.reject(new Error(msg));
  },
);

export const bookApi = {
  getAll: () => api.get("/api/books"),
  getByIsbn: (isbn) => api.get("/api/books/getbyisbn", { params: { isbn } }),
  getByAuthor: (author) =>
    api.get(`/api/books/findbyauthor/${encodeURIComponent(author)}`),
  totalBooks: () => api.get("/api/books/totalbooks"),
  checkAvailability: (title) =>
    api.get(`/api/books/${encodeURIComponent(title)}/availability`),
  add: (data) => api.post("/api/books/addbook", data),
  update: (id, data) => api.put(`/api/books/updatebook/${id}`, data),
  delete: (isbn) => api.delete(`/api/books/deletebook/${isbn}`),
  updateCopies: (title, n) =>
    api.patch(`/api/books/${encodeURIComponent(title)}/copies/${n}`),
  issue: (title) => api.patch(`/api/books/${encodeURIComponent(title)}/issue`),
  returnBook: (title) =>
    api.patch(`/api/books/${encodeURIComponent(title)}/return`),
};

export const userApi = {
  getAll: () => api.get("/api/users"),
  getById: (id) => api.get(`/api/users/getbyid/${id}`),
  checkStatus: (id) => api.get(`/api/users/${id}/checkstatus`),
  register: (data) => api.post("/api/users/register", data),
  update: (id, data) => api.put(`/api/users/updateUser/${id}`, data),
  delete: (id) => api.delete(`/api/users/deletebyid/${id}`),
};

export const loanApi = {
  getAll: () => api.get("/api/loan"),
  getByUser: (userId) => api.get(`/api/loan/${userId}/Loans`),
  borrow: (data) => api.post("/api/loan/borrowbook", data),
  returnLoan: (loanId) => api.put(`/api/loan/returnbook/${loanId}`),
};

export const fineApi = {
  getAll: () => api.get("/api/fines"),
  getPending: () => api.get("/api/fines/getallpendingfines"),
  create: (loanId) =>
    api.post("/api/fines/createfine", null, { params: { loanId } }),
};
