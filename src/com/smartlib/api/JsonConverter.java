package com.smartlib.api;

import com.smartlib.model.Book;
import com.smartlib.model.Loan;
import com.smartlib.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Simple JSON converter for API responses
 */
public class JsonConverter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String bookToJson(Book book) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"").append(escape(book.getId())).append("\",");
        json.append("\"title\":\"").append(escape(book.getTitle())).append("\",");
        json.append("\"author\":\"").append(escape(book.getAuthor())).append("\",");
        json.append("\"isbn\":\"").append(escape(book.getIsbn())).append("\",");
        json.append("\"barcode\":").append(book.getBarcode() != null ? "\"" + escape(book.getBarcode()) + "\"" : "null").append(",");
        json.append("\"category\":").append(book.getCategory() != null ? "\"" + escape(book.getCategory()) + "\"" : "null").append(",");
        json.append("\"description\":").append(book.getDescription() != null ? "\"" + escape(book.getDescription()) + "\"" : "null").append(",");
        json.append("\"available\":").append(book.isAvailable()).append(",");
        json.append("\"availableCopies\":").append(book.getAvailableCopies()).append(",");
        json.append("\"totalCopies\":").append(book.getTotalCopies()).append(",");
        json.append("\"tags\":[");
        List<String> tags = book.getTags();
        for (int i = 0; i < tags.size(); i++) {
            json.append("\"").append(escape(tags.get(i))).append("\"");
            if (i < tags.size() - 1) json.append(",");
        }
        json.append("]");
        json.append("}");
        return json.toString();
    }

    public static String booksToJson(List<Book> books) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < books.size(); i++) {
            json.append(bookToJson(books.get(i)));
            if (i < books.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    public static String userToJson(User user, List<Loan> loans) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"").append(escape(user.getId())).append("\",");
        json.append("\"name\":\"").append(escape(user.getName())).append("\",");
        json.append("\"email\":\"").append(escape(user.getEmail())).append("\",");
        json.append("\"phoneNumber\":").append(user.getPhoneNumber() != null ? "\"" + escape(user.getPhoneNumber()) + "\"" : "null").append(",");
        json.append("\"activeLoans\":").append(user.getActiveLoans()).append(",");
        json.append("\"registrationDate\":\"").append(user.getRegistrationDate().format(DATE_FORMATTER)).append("\"");
        if (loans != null) {
            json.append(",\"loans\":");
            json.append(loansToJson(loans));
        }
        json.append("}");
        return json.toString();
    }

    public static String usersToJson(List<User> users) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < users.size(); i++) {
            json.append(userToJson(users.get(i), null));
            if (i < users.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    public static String loanToJson(Loan loan) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"").append(escape(loan.getId())).append("\",");
        json.append("\"userId\":\"").append(escape(loan.getUserId())).append("\",");
        json.append("\"bookId\":\"").append(escape(loan.getBookId())).append("\",");
        json.append("\"borrowDate\":\"").append(loan.getBorrowDate().format(DATE_FORMATTER)).append("\",");
        json.append("\"dueDate\":\"").append(loan.getDueDate().format(DATE_FORMATTER)).append("\",");
        json.append("\"returnDate\":").append(loan.getReturnDate() != null ? "\"" + loan.getReturnDate().format(DATE_FORMATTER) + "\"" : "null").append(",");
        json.append("\"returned\":").append(loan.isReturned()).append(",");
        json.append("\"fineAmount\":").append(loan.getFineAmount()).append(",");
        json.append("\"isOverdue\":").append(loan.isOverdue()).append(",");
        json.append("\"daysRemaining\":").append(loan.getDaysRemaining());
        json.append("}");
        return json.toString();
    }

    public static String loansToJson(List<Loan> loans) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < loans.size(); i++) {
            json.append(loanToJson(loans.get(i)));
            if (i < loans.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    public static String analyticsToJson(Map<String, Object> analytics) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"totalBooks\":").append(analytics.get("totalBooks")).append(",");
        json.append("\"availableBooks\":").append(analytics.get("availableBooks")).append(",");
        json.append("\"totalUsers\":").append(analytics.get("totalUsers")).append(",");
        json.append("\"totalLoans\":").append(analytics.get("totalLoans")).append(",");
        json.append("\"activeLoans\":").append(analytics.get("activeLoans")).append(",");
        json.append("\"overdueLoans\":").append(analytics.get("overdueLoans"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> popularBooks = (List<Map<String, Object>>) analytics.get("popularBooks");
        if (popularBooks != null) {
            json.append(",\"popularBooks\":[");
            for (int i = 0; i < popularBooks.size(); i++) {
                Map<String, Object> book = popularBooks.get(i);
                json.append("{");
                json.append("\"title\":\"").append(escape((String) book.get("title"))).append("\",");
                json.append("\"author\":\"").append(escape((String) book.get("author"))).append("\",");
                json.append("\"loanCount\":").append(book.get("loanCount"));
                json.append("}");
                if (i < popularBooks.size() - 1) json.append(",");
            }
            json.append("]");
        }
        
        json.append("}");
        return json.toString();
    }

    public static Map<String, String> parseBookData(String json) {
        Map<String, String> data = new HashMap<>();
        // Simple JSON parsing (for MVP - in production use a proper JSON library)
        extractField(json, "id", data);
        extractField(json, "title", data);
        extractField(json, "author", data);
        extractField(json, "isbn", data);
        extractField(json, "barcode", data);
        extractField(json, "category", data);
        extractField(json, "description", data);
        extractField(json, "tags", data);
        return data;
    }

    public static Map<String, String> parseUserData(String json) {
        Map<String, String> data = new HashMap<>();
        extractField(json, "id", data);
        extractField(json, "name", data);
        extractField(json, "email", data);
        extractField(json, "phoneNumber", data);
        return data;
    }

    public static Map<String, String> parseBorrowData(String json) {
        Map<String, String> data = new HashMap<>();
        extractField(json, "userId", data);
        extractField(json, "bookId", data);
        return data;
    }

    public static Map<String, String> parseReturnData(String json) {
        Map<String, String> data = new HashMap<>();
        extractField(json, "loanId", data);
        return data;
    }

    private static void extractField(String json, String fieldName, Map<String, String> data) {
        String pattern = "\"" + fieldName + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            data.put(fieldName, m.group(1));
        }
    }

    private static String escape(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
