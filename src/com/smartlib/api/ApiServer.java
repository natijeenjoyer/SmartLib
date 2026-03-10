package com.smartlib.api;

import com.smartlib.service.LibraryService;

/**
 * API Server entry point
 */
public class ApiServer {
    public static void main(String[] args) {
        try {
            LibraryService libraryService = new LibraryService();
            HttpServer server = new HttpServer(libraryService);
            server.start();
            
            System.out.println("\n========================================");
            System.out.println("   SmartLib AI API Server Running");
            System.out.println("   http://localhost:8080");
            System.out.println("========================================\n");
            System.out.println("Press Ctrl+C to stop the server");
            
            // Keep server running
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
