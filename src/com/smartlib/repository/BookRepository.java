package com.smartlib.repository;

import com.smartlib.model.Book;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory repository for books
 * Can be replaced with database implementation later
 */
public class BookRepository {
    private Map<String, Book> books;
    private Map<String, Book> booksByBarcode;
    private Map<String, Book> booksByIsbn;

    public BookRepository() {
        this.books = new HashMap<>();
        this.booksByBarcode = new HashMap<>();
        this.booksByIsbn = new HashMap<>();
    }

    public void addBook(Book book) {
        books.put(book.getId(), book);
        if (book.getBarcode() != null && !book.getBarcode().isEmpty()) {
            booksByBarcode.put(book.getBarcode(), book);
        }
        if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            booksByIsbn.put(book.getIsbn(), book);
        }
    }

    public Book findById(String id) {
        return books.get(id);
    }

    public Book findByBarcode(String barcode) {
        return booksByBarcode.get(barcode);
    }

    public Book findByIsbn(String isbn) {
        return booksByIsbn.get(isbn);
    }

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public List<Book> searchByTitle(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return books.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return books.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<Book> searchByCategory(String category) {
        return books.values().stream()
                .filter(book -> category.equalsIgnoreCase(book.getCategory()))
                .collect(Collectors.toList());
    }

    public List<Book> searchByTag(String tag) {
        String lowerTag = tag.toLowerCase();
        return books.values().stream()
                .filter(book -> book.getTags().stream()
                        .anyMatch(t -> t.toLowerCase().contains(lowerTag)))
                .collect(Collectors.toList());
    }

    public List<Book> searchAvailable() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Book> search(String query) {
        String lowerQuery = query.toLowerCase();
        return books.values().stream()
                .filter(book -> 
                    book.getTitle().toLowerCase().contains(lowerQuery) ||
                    book.getAuthor().toLowerCase().contains(lowerQuery) ||
                    (book.getDescription() != null && book.getDescription().toLowerCase().contains(lowerQuery)) ||
                    book.getTags().stream().anyMatch(t -> t.toLowerCase().contains(lowerQuery)) ||
                    (book.getCategory() != null && book.getCategory().toLowerCase().contains(lowerQuery))
                )
                .collect(Collectors.toList());
    }

    public boolean removeBook(String id) {
        Book book = books.remove(id);
        if (book != null) {
            if (book.getBarcode() != null) {
                booksByBarcode.remove(book.getBarcode());
            }
            if (book.getIsbn() != null) {
                booksByIsbn.remove(book.getIsbn());
            }
            return true;
        }
        return false;
    }

    public int getTotalBooks() {
        return books.size();
    }

    public int getTotalAvailableBooks() {
        return (int) books.values().stream()
                .filter(Book::isAvailable)
                .count();
    }
}
