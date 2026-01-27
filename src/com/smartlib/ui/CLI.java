package com.smartlib.ui;

import com.smartlib.model.Book;
import com.smartlib.model.Loan;
import com.smartlib.model.User;
import com.smartlib.service.LibraryService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Command-line interface for SmartLib AI MVP
 */
public class CLI {
    private LibraryService libraryService;
    private Scanner scanner;

    public CLI() {
        this.libraryService = new LibraryService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("========================================");
        System.out.println("   Welcome to SmartLib AI MVP");
        System.out.println("   Intelligent Library Management System");
        System.out.println("========================================\n");

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        handleBookOperations();
                        break;
                    case "2":
                        handleUserOperations();
                        break;
                    case "3":
                        handleLoanOperations();
                        break;
                    case "4":
                        handleSearch();
                        break;
                    case "5":
                        displayAnalytics();
                        break;
                    case "6":
                        running = false;
                        System.out.println("Thank you for using SmartLib AI!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    private void printMainMenu() {
        System.out.println("Main Menu:");
        System.out.println("1. Book Operations");
        System.out.println("2. User Operations");
        System.out.println("3. Loan Operations");
        System.out.println("4. Search");
        System.out.println("5. Analytics Dashboard");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private void handleBookOperations() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Book Operations ---");
            System.out.println("1. Add Book");
            System.out.println("2. List All Books");
            System.out.println("3. List Available Books");
            System.out.println("4. View Book Details");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addBook();
                    break;
                case "2":
                    listAllBooks();
                    break;
                case "3":
                    listAvailableBooks();
                    break;
                case "4":
                    viewBookDetails();
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addBook() {
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        
        Book book = libraryService.addBook(id, title, author, isbn);
        System.out.println("Book added successfully: " + book);
    }

    private void listAllBooks() {
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("\nAll Books:");
            books.forEach(book -> System.out.println("  " + book));
        }
    }

    private void listAvailableBooks() {
        List<Book> books = libraryService.getAvailableBooks();
        if (books.isEmpty()) {
            System.out.println("No available books.");
        } else {
            System.out.println("\nAvailable Books:");
            books.forEach(book -> System.out.println("  " + book));
        }
    }

    private void viewBookDetails() {
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine().trim();
        Book book = libraryService.findBookById(id);
        if (book != null) {
            System.out.println("\nBook Details:");
            System.out.println("  ID: " + book.getId());
            System.out.println("  Title: " + book.getTitle());
            System.out.println("  Author: " + book.getAuthor());
            System.out.println("  ISBN: " + book.getIsbn());
            System.out.println("  Barcode: " + (book.getBarcode() != null ? book.getBarcode() : "N/A"));
            System.out.println("  Category: " + (book.getCategory() != null ? book.getCategory() : "N/A"));
            System.out.println("  Available: " + (book.isAvailable() ? "Yes" : "No"));
            System.out.println("  Copies: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
            if (!book.getTags().isEmpty()) {
                System.out.println("  Tags: " + String.join(", ", book.getTags()));
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    private void handleUserOperations() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- User Operations ---");
            System.out.println("1. Add User");
            System.out.println("2. List All Users");
            System.out.println("3. View User Details");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addUser();
                    break;
                case "2":
                    listAllUsers();
                    break;
                case "3":
                    viewUserDetails();
                    break;
                case "4":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addUser() {
        System.out.print("Enter User ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        
        User user = libraryService.addUser(id, name, email);
        System.out.println("User added successfully: " + user);
    }

    private void listAllUsers() {
        List<User> users = libraryService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("\nAll Users:");
            users.forEach(user -> System.out.println("  " + user));
        }
    }

    private void viewUserDetails() {
        System.out.print("Enter User ID: ");
        String id = scanner.nextLine().trim();
        User user = libraryService.findUserById(id);
        if (user != null) {
            System.out.println("\nUser Details:");
            System.out.println("  ID: " + user.getId());
            System.out.println("  Name: " + user.getName());
            System.out.println("  Email: " + user.getEmail());
            System.out.println("  Active Loans: " + user.getActiveLoans());
            
            List<Loan> loans = libraryService.getActiveUserLoans(id);
            if (!loans.isEmpty()) {
                System.out.println("\n  Active Loans:");
                loans.forEach(loan -> {
                    Book book = libraryService.findBookById(loan.getBookId());
                    System.out.println("    - " + (book != null ? book.getTitle() : "Unknown") + 
                                     " (Due: " + loan.getDueDate() + ", Days remaining: " + loan.getDaysRemaining() + ")");
                });
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private void handleLoanOperations() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Loan Operations ---");
            System.out.println("1. Borrow Book");
            System.out.println("2. Borrow Book by Barcode");
            System.out.println("3. Return Book");
            System.out.println("4. View User Loans");
            System.out.println("5. View Overdue Loans");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        borrowBook();
                        break;
                    case "2":
                        borrowBookByBarcode();
                        break;
                    case "3":
                        returnBook();
                        break;
                    case "4":
                        viewUserLoans();
                        break;
                    case "5":
                        viewOverdueLoans();
                        break;
                    case "6":
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void borrowBook() throws Exception {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine().trim();
        
        Loan loan = libraryService.borrowBook(userId, bookId);
        Book book = libraryService.findBookById(bookId);
        System.out.println("Book borrowed successfully!");
        System.out.println("  Book: " + (book != null ? book.getTitle() : bookId));
        System.out.println("  Due Date: " + loan.getDueDate());
        System.out.println("  Days until due: " + loan.getDaysRemaining());
    }

    private void borrowBookByBarcode() throws Exception {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();
        System.out.print("Enter Barcode: ");
        String barcode = scanner.nextLine().trim();
        
        Loan loan = libraryService.borrowBookByBarcode(userId, barcode);
        Book book = libraryService.findBookByBarcode(barcode);
        System.out.println("Book borrowed successfully!");
        System.out.println("  Book: " + (book != null ? book.getTitle() : "Unknown"));
        System.out.println("  Due Date: " + loan.getDueDate());
        System.out.println("  Days until due: " + loan.getDaysRemaining());
    }

    private void returnBook() throws Exception {
        System.out.print("Enter Loan ID: ");
        String loanId = scanner.nextLine().trim();
        
        Loan loan = libraryService.returnBook(loanId);
        Book book = libraryService.findBookById(loan.getBookId());
        System.out.println("Book returned successfully!");
        System.out.println("  Book: " + (book != null ? book.getTitle() : "Unknown"));
        if (loan.getFineAmount() > 0) {
            System.out.println("  Fine Amount: $" + String.format("%.2f", loan.getFineAmount()));
        }
    }

    private void viewUserLoans() {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();
        List<Loan> loans = libraryService.getUserLoans(userId);
        
        if (loans.isEmpty()) {
            System.out.println("No loans found for this user.");
        } else {
            System.out.println("\nUser Loans:");
            loans.forEach(loan -> {
                Book book = libraryService.findBookById(loan.getBookId());
                System.out.println("  Loan ID: " + loan.getId());
                System.out.println("    Book: " + (book != null ? book.getTitle() : "Unknown"));
                System.out.println("    Status: " + (loan.isReturned() ? "Returned" : 
                    (loan.isOverdue() ? "Overdue" : "Active")));
                System.out.println("    Due Date: " + loan.getDueDate());
                if (!loan.isReturned()) {
                    System.out.println("    Days Remaining: " + loan.getDaysRemaining());
                }
                if (loan.getFineAmount() > 0) {
                    System.out.println("    Fine: $" + String.format("%.2f", loan.getFineAmount()));
                }
                System.out.println();
            });
        }
    }

    private void viewOverdueLoans() {
        List<Loan> overdueLoans = libraryService.getOverdueLoans();
        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue loans.");
        } else {
            System.out.println("\nOverdue Loans:");
            overdueLoans.forEach(loan -> {
                Book book = libraryService.findBookById(loan.getBookId());
                User user = libraryService.findUserById(loan.getUserId());
                System.out.println("  Loan ID: " + loan.getId());
                System.out.println("    User: " + (user != null ? user.getName() : "Unknown"));
                System.out.println("    Book: " + (book != null ? book.getTitle() : "Unknown"));
                System.out.println("    Due Date: " + loan.getDueDate());
                System.out.println("    Days Overdue: " + loan.getDaysOverdue());
                loan.calculateFine();
                System.out.println("    Fine: $" + String.format("%.2f", loan.getFineAmount()));
                System.out.println();
            });
        }
    }

    private void handleSearch() {
        System.out.print("Enter search query: ");
        String query = scanner.nextLine().trim();
        
        List<Book> results = libraryService.searchBooks(query);
        if (results.isEmpty()) {
            System.out.println("No books found matching: " + query);
        } else {
            System.out.println("\nSearch Results (" + results.size() + " found):");
            results.forEach(book -> {
                System.out.println("  " + book);
                if (book.getCategory() != null) {
                    System.out.println("    Category: " + book.getCategory());
                }
            });
        }
    }

    private void displayAnalytics() {
        Map<String, Object> analytics = libraryService.getAnalytics();
        
        System.out.println("\n========================================");
        System.out.println("   Analytics Dashboard");
        System.out.println("========================================");
        System.out.println("Total Books: " + analytics.get("totalBooks"));
        System.out.println("Available Books: " + analytics.get("availableBooks"));
        System.out.println("Total Users: " + analytics.get("totalUsers"));
        System.out.println("Total Loans: " + analytics.get("totalLoans"));
        System.out.println("Active Loans: " + analytics.get("activeLoans"));
        System.out.println("Overdue Loans: " + analytics.get("overdueLoans"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> popularBooks = (List<Map<String, Object>>) analytics.get("popularBooks");
        if (popularBooks != null && !popularBooks.isEmpty()) {
            System.out.println("\nMost Popular Books:");
            for (int i = 0; i < popularBooks.size(); i++) {
                Map<String, Object> bookInfo = popularBooks.get(i);
                System.out.println("  " + (i + 1) + ". " + bookInfo.get("title") + 
                                 " by " + bookInfo.get("author") + 
                                 " (" + bookInfo.get("loanCount") + " loans)");
            }
        }
        System.out.println("========================================\n");
    }
}
