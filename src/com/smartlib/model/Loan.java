package com.smartlib.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a loan transaction in the library system
 */
public class Loan {
    private String id;
    private String userId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;
    private double fineAmount; // For overdue fines

    // Standard loan period in days
    private static final int STANDARD_LOAN_PERIOD = 14;

    public Loan(String id, String userId, String bookId) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = LocalDate.now();
        this.dueDate = this.borrowDate.plusDays(STANDARD_LOAN_PERIOD);
        this.isReturned = false;
        this.fineAmount = 0.0;
    }

    public Loan(String id, String userId, String bookId, LocalDate borrowDate, int loanPeriodDays) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = this.borrowDate.plusDays(loanPeriodDays);
        this.isReturned = false;
        this.fineAmount = 0.0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
        if (returned && returnDate == null) {
            returnDate = LocalDate.now();
            calculateFine();
        }
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public boolean isOverdue() {
        return !isReturned && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public void calculateFine() {
        if (isOverdue()) {
            // Simple fine calculation: $0.50 per day overdue
            long daysOverdue = getDaysOverdue();
            this.fineAmount = daysOverdue * 0.50;
        }
    }

    public int getDaysRemaining() {
        if (isReturned) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        return (int) Math.max(0, days);
    }

    @Override
    public String toString() {
        String status = isReturned ? "Returned" : (isOverdue() ? "Overdue" : "Active");
        return String.format("Loan{id='%s', userId='%s', bookId='%s', status='%s', dueDate=%s, fine=$%.2f}",
                id, userId, bookId, status, dueDate, fineAmount);
    }
}
