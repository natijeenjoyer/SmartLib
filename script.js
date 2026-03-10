// Data storage (will be populated from API)
let books = [];
let users = [];
let loans = [];

// Navigation
document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const section = btn.getAttribute('data-section');
        switchSection(section);
        
        document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
    });
});

function switchSection(sectionId) {
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
        
        // Load section-specific data
        if (sectionId === 'books') {
            loadBooks();
        } else if (sectionId === 'users') {
            loadUsers();
        } else if (sectionId === 'loans') {
            loadLoans();
        } else if (sectionId === 'analytics') {
            loadAnalytics();
        }
    }
}

// Search functionality
document.getElementById('searchBtn').addEventListener('click', performSearch);
document.getElementById('searchInput').addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        performSearch();
    }
});

async function performSearch() {
    const query = document.getElementById('searchInput').value.trim();
    const resultsContainer = document.getElementById('searchResults');
    
    if (!query) {
        resultsContainer.innerHTML = '<p style="color: var(--text-secondary);">Please enter a search query.</p>';
        return;
    }
    
    try {
        const results = await api.searchBooks(query);
        
        if (results.length === 0) {
            resultsContainer.innerHTML = '<p style="color: var(--text-secondary);">No books found matching your search.</p>';
            return;
        }
        
        resultsContainer.innerHTML = results.map(book => `
            <div class="book-card">
                <h3>${book.title}</h3>
                <p><strong>Author:</strong> ${book.author}</p>
                <p><strong>Category:</strong> ${book.category || 'N/A'}</p>
                <p><strong>ISBN:</strong> ${book.isbn}</p>
                <p><strong>Description:</strong> ${book.description || 'N/A'}</p>
                <div class="book-status ${book.available ? 'status-available' : 'status-unavailable'}">
                    ${book.available ? 'Available' : 'Unavailable'}
                </div>
            </div>
        `).join('');
    } catch (error) {
        resultsContainer.innerHTML = `<p style="color: #f87171;">Error: ${error.message}</p>`;
    }
}

// Books section
document.getElementById('addBookBtn').addEventListener('click', () => {
    document.getElementById('addBookModal').classList.add('active');
});

async function loadBooks() {
    const booksGrid = document.getElementById('booksGrid');
    try {
        books = await api.getBooks();
        booksGrid.innerHTML = books.map(book => `
            <div class="book-card">
                <h3>${book.title}</h3>
                <p><strong>Author:</strong> ${book.author}</p>
                <p><strong>Category:</strong> ${book.category || 'N/A'}</p>
                <p><strong>ISBN:</strong> ${book.isbn}</p>
                <p><strong>Barcode:</strong> ${book.barcode || 'N/A'}</p>
                <p><strong>Copies:</strong> ${book.availableCopies}/${book.totalCopies}</p>
                <div class="book-status ${book.available ? 'status-available' : 'status-unavailable'}">
                    ${book.available ? 'Available' : 'Unavailable'}
                </div>
            </div>
        `).join('');
    } catch (error) {
        booksGrid.innerHTML = `<p style="color: #f87171;">Error loading books: ${error.message}</p>`;
    }
}

document.getElementById('addBookForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    try {
        const newBook = {
            id: document.getElementById('bookId').value,
            title: document.getElementById('bookTitle').value,
            author: document.getElementById('bookAuthor').value,
            isbn: document.getElementById('bookIsbn').value,
            barcode: document.getElementById('bookBarcode').value || null,
            category: document.getElementById('bookCategory').value || null,
            description: document.getElementById('bookDescription').value || null,
            tags: document.getElementById('bookTags').value
        };
        
        await api.addBook(newBook);
        document.getElementById('addBookModal').classList.remove('active');
        document.getElementById('addBookForm').reset();
        await loadBooks();
        showNotification('Book added successfully!');
    } catch (error) {
        showNotification('Error adding book: ' + error.message, 'error');
    }
});

// Users section
document.getElementById('addUserBtn').addEventListener('click', () => {
    document.getElementById('addUserModal').classList.add('active');
});

async function loadUsers() {
    const usersList = document.getElementById('usersList');
    try {
        users = await api.getUsers();
        usersList.innerHTML = users.map(user => `
            <div class="user-card">
                <h3>${user.name}</h3>
                <p><strong>ID:</strong> ${user.id}</p>
                <p><strong>Email:</strong> ${user.email}</p>
                <p><strong>Active Loans:</strong> ${user.activeLoans}</p>
            </div>
        `).join('');
    } catch (error) {
        usersList.innerHTML = `<p style="color: #f87171;">Error loading users: ${error.message}</p>`;
    }
}

document.getElementById('addUserForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    try {
        const newUser = {
            id: document.getElementById('userId').value,
            name: document.getElementById('userName').value,
            email: document.getElementById('userEmail').value,
            phoneNumber: document.getElementById('userPhone').value || null
        };
        
        await api.addUser(newUser);
        document.getElementById('addUserModal').classList.remove('active');
        document.getElementById('addUserForm').reset();
        await loadUsers();
        showNotification('User added successfully!');
    } catch (error) {
        showNotification('Error adding user: ' + error.message, 'error');
    }
});

// Loans section
document.getElementById('borrowBtn').addEventListener('click', borrowBook);
document.getElementById('returnBtn').addEventListener('click', returnBook);

// Function to return book by ID (called from loan card button)
async function returnBookById(loanId) {
    document.getElementById('returnLoanId').value = loanId;
    await returnBook();
}

async function borrowBook() {
    const userId = document.getElementById('borrowUserId').value.trim();
    const bookIdOrBarcode = document.getElementById('borrowBookId').value.trim();
    
    if (!userId || !bookIdOrBarcode) {
        showNotification('Please fill in all fields', 'error');
        return;
    }
    
    try {
        // First, try to find book by ID or barcode
        let bookId = bookIdOrBarcode;
        try {
            const book = await api.getBook(bookIdOrBarcode);
            bookId = book.id;
        } catch (e) {
            // If not found by ID, try to find by barcode in the books list
            if (books.length === 0) {
                books = await api.getBooks();
            }
            const book = books.find(b => b.barcode === bookIdOrBarcode);
            if (book) {
                bookId = book.id;
            } else {
                throw new Error('Book not found');
            }
        }
        
        const loan = await api.borrowBook(userId, bookId);
        document.getElementById('borrowUserId').value = '';
        document.getElementById('borrowBookId').value = '';
        await loadLoans();
        await loadBooks(); // Refresh books to update availability
        showNotification('Book borrowed successfully!');
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

async function returnBook() {
    const loanId = document.getElementById('returnLoanId').value.trim();
    
    if (!loanId) {
        showNotification('Please enter a loan ID', 'error');
        return;
    }
    
    try {
        const loan = await api.returnBook(loanId);
        document.getElementById('returnLoanId').value = '';
        await loadLoans();
        await loadBooks(); // Refresh books to update availability
        await loadUsers(); // Refresh users to update active loans
        showNotification(`Book returned successfully!${loan.fineAmount > 0 ? ` Fine: $${loan.fineAmount.toFixed(2)}` : ''}`);
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

async function loadLoans() {
    const loansList = document.getElementById('loansList');
    try {
        // Get all loans (active ones)
        loans = await api.getLoans();
        const activeLoans = loans.filter(l => !l.returned);
        
        if (activeLoans.length === 0) {
            loansList.innerHTML = '<p style="color: var(--text-secondary);">No active loans.</p>';
            return;
        }
        
        // Fetch book and user details for each loan
        if (books.length === 0) {
            books = await api.getBooks();
        }
        if (users.length === 0) {
            users = await api.getUsers();
        }
        
        loansList.innerHTML = await Promise.all(activeLoans.map(async (loan) => {
            const book = books.find(b => b.id === loan.bookId) || { title: 'Unknown Book' };
            const user = users.find(u => u.id === loan.userId) || { name: 'Unknown User' };
            const dueDate = new Date(loan.dueDate);
            const today = new Date();
            const isOverdue = today > dueDate;
            const daysRemaining = loan.daysRemaining !== undefined ? loan.daysRemaining : Math.ceil((dueDate - today) / (1000 * 60 * 60 * 24));
            
            return `
                <div class="loan-card">
                    <div class="loan-info">
                        <h4>${book.title}</h4>
                        <p><strong>Loan ID:</strong> <span class="loan-id-display">${loan.id}</span></p>
                        <p><strong>Borrowed by:</strong> ${user.name}</p>
                        <p><strong>Borrow Date:</strong> ${loan.borrowDate}</p>
                        <p><strong>Due Date:</strong> ${loan.dueDate}</p>
                        <p><strong>Days ${isOverdue ? 'Overdue' : 'Remaining'}:</strong> ${Math.abs(daysRemaining)}</p>
                    </div>
                    <div class="loan-actions-right">
                        <div class="loan-status ${isOverdue ? 'status-overdue' : 'status-active'}">
                            ${isOverdue ? 'Overdue' : 'Active'}
                        </div>
                        <button class="btn-return" onclick="returnBookById('${loan.id}')">Return Book</button>
                    </div>
                </div>
            `;
        })).then(html => html.join(''));
    } catch (error) {
        loansList.innerHTML = `<p style="color: #f87171;">Error loading loans: ${error.message}</p>`;
    }
}

// Analytics section
async function loadAnalytics() {
    try {
        const analytics = await api.getAnalytics();
        
        document.getElementById('totalBooks').textContent = analytics.totalBooks;
        document.getElementById('availableBooks').textContent = analytics.availableBooks;
        document.getElementById('totalUsers').textContent = analytics.totalUsers;
        document.getElementById('activeLoans').textContent = analytics.activeLoans;
        document.getElementById('overdueLoans').textContent = analytics.overdueLoans;
        document.getElementById('totalLoans').textContent = analytics.totalLoans;
        
        // Popular books
        const popularList = document.getElementById('popularBooksList');
        if (analytics.popularBooks && analytics.popularBooks.length > 0) {
            popularList.innerHTML = analytics.popularBooks.map(item => `
                <div class="popular-item">
                    <div>
                        <h4>${item.title}</h4>
                        <p>by ${item.author}</p>
                    </div>
                    <div class="loan-count">${item.loanCount}</div>
                </div>
            `).join('');
        } else {
            popularList.innerHTML = '<p style="color: var(--text-secondary);">No loan data available yet.</p>';
        }
    } catch (error) {
        console.error('Error loading analytics:', error);
        showNotification('Error loading analytics: ' + error.message, 'error');
    }
}

// Modal handling
document.querySelectorAll('.close').forEach(closeBtn => {
    closeBtn.addEventListener('click', () => {
        closeBtn.closest('.modal').classList.remove('active');
    });
});

window.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal')) {
        e.target.classList.remove('active');
    }
});

// Notification system
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 2rem;
        right: 2rem;
        padding: 1rem 2rem;
        background: ${type === 'error' ? 'rgba(255, 0, 0, 0.2)' : 'rgba(255, 140, 0, 0.2)'};
        border: 1px solid ${type === 'error' ? 'rgba(255, 0, 0, 0.5)' : 'rgba(255, 140, 0, 0.5)'};
        border-radius: 10px;
        color: var(--text-primary);
        z-index: 2000;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Initialize - check API connection
async function initialize() {
    try {
        // Test API connection
        await api.getBooks();
        await loadBooks();
        await loadUsers();
        await loadLoans();
        await loadAnalytics();
    } catch (error) {
        console.error('API connection failed:', error);
        showNotification('Cannot connect to API server. Make sure the Java backend is running on http://localhost:8080', 'error');
        // Still try to load with empty data
        document.getElementById('booksGrid').innerHTML = '<p style="color: #f87171;">API server not available. Please start the backend server.</p>';
    }
}

// Initialize on page load
initialize();
