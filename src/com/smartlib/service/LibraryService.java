package com.smartlib.service;

import com.smartlib.model.Book;
import com.smartlib.model.Loan;
import com.smartlib.model.User;
import com.smartlib.repository.BookRepository;
import com.smartlib.repository.LoanRepository;
import com.smartlib.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main service layer for library operations
 */
public class LibraryService {
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private LoanRepository loanRepository;

    public LibraryService() {
        this.bookRepository = new BookRepository();
        this.userRepository = new UserRepository();
        this.loanRepository = new LoanRepository();
        initializeSampleData();
    }

    // Book Operations
    public Book addBook(String id, String title, String author, String isbn) {
        Book book = new Book(id, title, author, isbn);
        bookRepository.addBook(book);
        return book;
    }

    public Book findBookById(String id) {
        return bookRepository.findById(id);
    }

    public Book findBookByBarcode(String barcode) {
        return bookRepository.findByBarcode(barcode);
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.search(query);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.searchAvailable();
    }

    // User Operations
    public User addUser(String id, String name, String email) {
        User user = new User(id, name, email);
        userRepository.addUser(user);
        return user;
    }

    public User findUserById(String id) {
        return userRepository.findById(id);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Loan Operations
    public Loan borrowBook(String userId, String bookId) throws Exception {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new Exception("User not found: " + userId);
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new Exception("Book not found: " + bookId);
        }

        if (!book.isAvailable()) {
            throw new Exception("Book is not available: " + book.getTitle());
        }

        // Check if user already has this book
        List<Loan> activeLoans = loanRepository.findActiveLoansByUserId(userId);
        boolean alreadyBorrowed = activeLoans.stream()
                .anyMatch(loan -> loan.getBookId().equals(bookId));
        
        if (alreadyBorrowed) {
            throw new Exception("User already has this book on loan");
        }

        String loanId = "LOAN-" + System.currentTimeMillis();
        Loan loan = new Loan(loanId, userId, bookId);
        
        book.borrowCopy();
        user.incrementActiveLoans();
        loanRepository.addLoan(loan);

        return loan;
    }

    public Loan borrowBookByBarcode(String userId, String barcode) throws Exception {
        Book book = bookRepository.findByBarcode(barcode);
        if (book == null) {
            throw new Exception("Book not found with barcode: " + barcode);
        }
        return borrowBook(userId, book.getId());
    }

    public Loan returnBook(String loanId) throws Exception {
        Loan loan = loanRepository.findById(loanId);
        if (loan == null) {
            throw new Exception("Loan not found: " + loanId);
        }

        if (loan.isReturned()) {
            throw new Exception("Book already returned");
        }

        Book book = bookRepository.findById(loan.getBookId());
        if (book != null) {
            book.returnCopy();
        }

        User user = userRepository.findById(loan.getUserId());
        if (user != null) {
            user.decrementActiveLoans();
        }

        loan.setReturned(true);
        loan.calculateFine();
        loanRepository.updateLoan(loan);

        return loan;
    }

    public List<Loan> getUserLoans(String userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getActiveUserLoans(String userId) {
        return loanRepository.findActiveLoansByUserId(userId);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    // Analytics
    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalBooks", bookRepository.getTotalBooks());
        analytics.put("availableBooks", bookRepository.getTotalAvailableBooks());
        analytics.put("totalUsers", userRepository.getTotalUsers());
        analytics.put("totalLoans", loanRepository.getTotalLoans());
        analytics.put("activeLoans", loanRepository.getActiveLoansCount());
        analytics.put("overdueLoans", loanRepository.findOverdueLoans().size());
        
        // Most popular books (by number of loans)
        Map<String, Long> bookLoanCounts = loanRepository.findAll().stream()
                .collect(Collectors.groupingBy(Loan::getBookId, Collectors.counting()));
        
        List<Map.Entry<String, Long>> sortedBooks = bookLoanCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
        
        List<Map<String, Object>> popularBooks = new ArrayList<>();
        for (Map.Entry<String, Long> entry : sortedBooks) {
            Book book = bookRepository.findById(entry.getKey());
            if (book != null) {
                Map<String, Object> bookInfo = new HashMap<>();
                bookInfo.put("title", book.getTitle());
                bookInfo.put("author", book.getAuthor());
                bookInfo.put("loanCount", entry.getValue());
                popularBooks.add(bookInfo);
            }
        }
        analytics.put("popularBooks", popularBooks);
        
        return analytics;
    }

    // Initialize sample data for MVP demonstration
    private void initializeSampleData() {
        // Add sample books
        Book book1 = addBook("B001", "Introduction to Java Programming", "John Smith", "978-0134685991");
        book1.setCategory("Programming");
        book1.setDescription("A comprehensive guide to Java programming");
        book1.addTag("programming");
        book1.addTag("java");
        book1.addTag("computer science");
        book1.setBarcode("BC001");

        Book book2 = addBook("B002", "Data Structures and Algorithms", "Jane Doe", "978-0262530910");
        book2.setCategory("Computer Science");
        book2.setDescription("Essential algorithms and data structures");
        book2.addTag("algorithms");
        book2.addTag("data structures");
        book2.addTag("computer science");
        book2.setBarcode("BC002");

        Book book3 = addBook("B003", "Machine Learning Basics", "Robert Johnson", "978-0134093416");
        book3.setCategory("Artificial Intelligence");
        book3.setDescription("Introduction to machine learning concepts");
        book3.addTag("machine learning");
        book3.addTag("AI");
        book3.addTag("data science");
        book3.setBarcode("BC003");

        Book book4 = addBook("B004", "Database Systems", "Emily Brown", "978-0133970777");
        book4.setCategory("Database");
        book4.setDescription("Fundamentals of database management");
        book4.addTag("database");
        book4.addTag("SQL");
        book4.addTag("data management");
        book4.setBarcode("BC004");

        // Add sample users
        addUser("U001", "Alice Johnson", "alice@example.com");
        addUser("U002", "Bob Williams", "bob@example.com");
        addUser("U003", "Charlie Davis", "charlie@example.com");
    }
}
