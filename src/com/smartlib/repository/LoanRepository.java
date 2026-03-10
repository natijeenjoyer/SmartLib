package com.smartlib.repository;

import com.smartlib.model.Loan;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory repository for loans
 */
public class LoanRepository {
    private Map<String, Loan> loans;
    private Map<String, List<Loan>> loansByUserId;
    private Map<String, List<Loan>> loansByBookId;

    public LoanRepository() {
        this.loans = new HashMap<>();
        this.loansByUserId = new HashMap<>();
        this.loansByBookId = new HashMap<>();
    }

    public void addLoan(Loan loan) {
        loans.put(loan.getId(), loan);
        
        loansByUserId.computeIfAbsent(loan.getUserId(), k -> new ArrayList<>()).add(loan);
        loansByBookId.computeIfAbsent(loan.getBookId(), k -> new ArrayList<>()).add(loan);
    }

    public Loan findById(String id) {
        return loans.get(id);
    }

    public List<Loan> findByUserId(String userId) {
        return loansByUserId.getOrDefault(userId, new ArrayList<>());
    }

    public List<Loan> findByBookId(String bookId) {
        return loansByBookId.getOrDefault(bookId, new ArrayList<>());
    }

    public List<Loan> findActiveLoansByUserId(String userId) {
        return findByUserId(userId).stream()
                .filter(loan -> !loan.isReturned())
                .collect(Collectors.toList());
    }

    public List<Loan> findOverdueLoans() {
        return loans.values().stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }

    public List<Loan> findAll() {
        return new ArrayList<>(loans.values());
    }

    public List<Loan> findActiveLoans() {
        return loans.values().stream()
                .filter(loan -> !loan.isReturned())
                .collect(Collectors.toList());
    }

    public void updateLoan(Loan loan) {
        // Since we're using in-memory storage, the loan object is already updated
        // This method is here for future database implementation compatibility
    }

    public int getTotalLoans() {
        return loans.size();
    }

    public int getActiveLoansCount() {
        return (int) loans.values().stream()
                .filter(loan -> !loan.isReturned())
                .count();
    }
}
