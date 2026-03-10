package com.smartlib.api;

import com.smartlib.service.LibraryService;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Simple HTTP server for REST API
 */
public class HttpServer {
    private LibraryService libraryService;
    private com.sun.net.httpserver.HttpServer server;
    private static final int PORT = 8080;

    public HttpServer(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    public void start() throws IOException {
        server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Enable CORS for all routes
        server.createContext("/", new CorsHandler(new ApiHandler()));
        
        server.setExecutor(null);
        server.start();
        System.out.println("SmartLib API Server started on http://localhost:" + PORT);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            try {
                // Route handling
                if (path.equals("/api/books") && method.equals("GET")) {
                    handleGetBooks(exchange);
                } else if (path.equals("/api/books") && method.equals("POST")) {
                    handleAddBook(exchange);
                } else if (path.equals("/api/books/search") && method.equals("GET")) {
                    handleSearchBooks(exchange);
                } else if (path.startsWith("/api/books/") && method.equals("GET")) {
                    String bookId = path.substring("/api/books/".length());
                    handleGetBook(exchange, bookId);
                } else if (path.equals("/api/users") && method.equals("GET")) {
                    handleGetUsers(exchange);
                } else if (path.equals("/api/users") && method.equals("POST")) {
                    handleAddUser(exchange);
                } else if (path.startsWith("/api/users/") && method.equals("GET")) {
                    String userId = path.substring("/api/users/".length());
                    handleGetUser(exchange, userId);
                } else if (path.equals("/api/loans") && method.equals("GET")) {
                    handleGetLoans(exchange);
                } else if (path.equals("/api/loans/borrow") && method.equals("POST")) {
                    handleBorrowBook(exchange);
                } else if (path.equals("/api/loans/return") && method.equals("POST")) {
                    handleReturnBook(exchange);
                } else if (path.equals("/api/analytics") && method.equals("GET")) {
                    handleGetAnalytics(exchange);
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    // CORS Handler wrapper
    private class CorsHandler implements HttpHandler {
        private HttpHandler handler;

        public CorsHandler(HttpHandler handler) {
            this.handler = handler;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }
            
            handler.handle(exchange);
        }
    }

    private void handleGetBooks(HttpExchange exchange) throws IOException {
        String json = JsonConverter.booksToJson(libraryService.getAllBooks());
        sendResponse(exchange, 200, json);
    }

    private void handleGetBook(HttpExchange exchange, String bookId) throws IOException {
        var book = libraryService.findBookById(bookId);
        if (book != null) {
            String json = JsonConverter.bookToJson(book);
            sendResponse(exchange, 200, json);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Book not found\"}");
        }
    }

    private void handleSearchBooks(HttpExchange exchange) throws IOException {
        String query = getQueryParam(exchange, "q");
        if (query == null || query.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"Query parameter 'q' is required\"}");
            return;
        }
        String json = JsonConverter.booksToJson(libraryService.searchBooks(query));
        sendResponse(exchange, 200, json);
    }

    private void handleAddBook(HttpExchange exchange) throws IOException {
        String body = getRequestBody(exchange);
        var bookData = JsonConverter.parseBookData(body);
        
        var book = libraryService.addBook(
            bookData.get("id"),
            bookData.get("title"),
            bookData.get("author"),
            bookData.get("isbn")
        );
        
        // Set additional properties
        if (bookData.containsKey("barcode")) {
            book.setBarcode(bookData.get("barcode"));
        }
        if (bookData.containsKey("category")) {
            book.setCategory(bookData.get("category"));
        }
        if (bookData.containsKey("description")) {
            book.setDescription(bookData.get("description"));
        }
        if (bookData.containsKey("tags")) {
            String[] tags = bookData.get("tags").split(",");
            for (String tag : tags) {
                book.addTag(tag.trim());
            }
        }
        
        String json = JsonConverter.bookToJson(book);
        sendResponse(exchange, 201, json);
    }

    private void handleGetUsers(HttpExchange exchange) throws IOException {
        String json = JsonConverter.usersToJson(libraryService.getAllUsers());
        sendResponse(exchange, 200, json);
    }

    private void handleGetUser(HttpExchange exchange, String userId) throws IOException {
        var user = libraryService.findUserById(userId);
        if (user != null) {
            var loans = libraryService.getActiveUserLoans(userId);
            String json = JsonConverter.userToJson(user, loans);
            sendResponse(exchange, 200, json);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"User not found\"}");
        }
    }

    private void handleAddUser(HttpExchange exchange) throws IOException {
        String body = getRequestBody(exchange);
        var userData = JsonConverter.parseUserData(body);
        
        var user = libraryService.addUser(
            userData.get("id"),
            userData.get("name"),
            userData.get("email")
        );
        
        if (userData.containsKey("phoneNumber")) {
            user.setPhoneNumber(userData.get("phoneNumber"));
        }
        
        String json = JsonConverter.userToJson(user, null);
        sendResponse(exchange, 201, json);
    }

    private void handleGetLoans(HttpExchange exchange) throws IOException {
        String userId = getQueryParam(exchange, "userId");
        if (userId != null) {
            String json = JsonConverter.loansToJson(libraryService.getUserLoans(userId));
            sendResponse(exchange, 200, json);
        } else {
            // Default: return all active loans (for the UI loans page)
            String json = JsonConverter.loansToJson(libraryService.getOverdueLoans());
            sendResponse(exchange, 200, json);
        }
    }

    private void handleBorrowBook(HttpExchange exchange) throws IOException {
        String body = getRequestBody(exchange);
        var data = JsonConverter.parseBorrowData(body);
        
        try {
            String bookIdOrBarcode = data.get("bookId");
            // Try to find by barcode first, then by ID
            var book = libraryService.findBookByBarcode(bookIdOrBarcode);
            String bookId = book != null ? book.getId() : bookIdOrBarcode;
            
            var loan = libraryService.borrowBook(data.get("userId"), bookId);
            String json = JsonConverter.loanToJson(loan);
            sendResponse(exchange, 201, json);
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleReturnBook(HttpExchange exchange) throws IOException {
        String body = getRequestBody(exchange);
        var data = JsonConverter.parseReturnData(body);
        
        try {
            var loan = libraryService.returnBook(data.get("loanId"));
            String json = JsonConverter.loanToJson(loan);
            sendResponse(exchange, 200, json);
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetAnalytics(HttpExchange exchange) throws IOException {
        String json = JsonConverter.analyticsToJson(libraryService.getAnalytics());
        sendResponse(exchange, 200, json);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private String getRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    private String getQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
