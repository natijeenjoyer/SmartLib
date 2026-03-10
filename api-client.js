// API Client for SmartLib Backend
const API_BASE_URL = 'http://localhost:8080/api';

class ApiClient {
    async request(endpoint, options = {}) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });
            
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || 'Request failed');
            }
            
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // Books
    async getBooks() {
        return this.request('/books');
    }

    async getBook(id) {
        return this.request(`/books/${id}`);
    }

    async searchBooks(query) {
        return this.request(`/books/search?q=${encodeURIComponent(query)}`);
    }

    async addBook(bookData) {
        return this.request('/books', {
            method: 'POST',
            body: JSON.stringify(bookData)
        });
    }

    // Users
    async getUsers() {
        return this.request('/users');
    }

    async getUser(id) {
        return this.request(`/users/${id}`);
    }

    async addUser(userData) {
        return this.request('/users', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    // Loans
    async getLoans(userId = null) {
        const endpoint = userId ? `/loans?userId=${userId}` : '/loans';
        return this.request(endpoint);
    }

    async borrowBook(userId, bookId) {
        return this.request('/loans/borrow', {
            method: 'POST',
            body: JSON.stringify({ userId, bookId })
        });
    }

    async returnBook(loanId) {
        return this.request('/loans/return', {
            method: 'POST',
            body: JSON.stringify({ loanId })
        });
    }

    // Analytics
    async getAnalytics() {
        return this.request('/analytics');
    }
}

// Global API client instance
const api = new ApiClient();
