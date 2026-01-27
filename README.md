# SmartLib AI MVP

An intelligent library management system that integrates artificial intelligence capabilities to enhance search, recommendations, management of printed and electronic resource loans, due date tracking, and collection usage analytics.

## Overview

SmartLib AI addresses common challenges in library management:
- **Semantic Search**: Natural language processing for finding relevant materials
- **Loan Management**: Barcode/RFID support for efficient resource tracking
- **Due Date Tracking**: Automated tracking and fine calculation
- **Analytics Dashboard**: Data-driven insights on collection popularity and user behavior

## Project Status

This is an **intermediate MVP** - a partially functional prototype that demonstrates core functionality with basic user interaction and data collection capabilities.

## Features

### Current MVP Features

1. **Book Management**
   - Add books with metadata (title, author, ISBN, barcode, category, tags)
   - Search books by title, author, category, tags, or full-text search
   - Track book availability and copies
   - View book details

2. **User Management**
   - Register and manage library users
   - Track user preferences (for future recommendation engine)
   - View user loan history

3. **Loan Management**
   - Borrow books by ID or barcode
   - Return books with automatic fine calculation
   - Track due dates and overdue items
   - View active and historical loans

4. **Search Functionality**
   - Basic semantic search across titles, authors, descriptions, categories, and tags
   - Search available books
   - Filter by category

5. **Analytics Dashboard**
   - Total books and availability statistics
   - User and loan statistics
   - Overdue loan tracking
   - Most popular books (by loan count)

## Project Structure

```
smartLibMvp/
├── src/                   # Java backend source code
│   ├── com/
│   │   └── smartlib/
│   │       ├── model/          # Domain models
│   │       │   ├── Book.java
│   │       │   ├── User.java
│   │       │   └── Loan.java
│   │       ├── repository/     # Data access layer (in-memory)
│   │       │   ├── BookRepository.java
│   │       │   ├── UserRepository.java
│   │       │   └── LoanRepository.java
│   │       ├── service/        # Business logic layer
│   │       │   └── LibraryService.java
│   │       └── ui/            # CLI interface
│   │           └── CLI.java
│   └── App.java               # CLI Application entry point
└── web/                    # Frontend web application
    ├── index.html          # Main HTML file
    ├── styles.css          # Styling with orange fade theme
    └── script.js           # Frontend JavaScript logic
```

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- VS Code with Java Extension Pack (recommended)

### Running the Application

#### Option 1: Full Stack (Frontend + Backend API) - Recommended

**Step 1: Start the Java API Server**

1. Compile the API server:
   ```bash
   javac -d bin -sourcepath src src/com/smartlib/api/ApiServer.java
   ```

2. Run the API server:
   ```bash
   java -cp bin com.smartlib.api.ApiServer
   ```

   The server will start on `http://localhost:8080`

**Step 2: Start the Web Frontend**

1. Navigate to the `web` directory:
   ```bash
   cd web
   ```

2. Use VS Code's Live Server extension:
   - Right-click on `index.html`
   - Select "Open with Live Server"
   - The application will open in your browser

   Or use any local web server:
   ```bash
   # Using Python
   python -m http.server 8000
   
   # Using Node.js (http-server)
   npx http-server
   ```

3. Open `http://localhost:8000` (or the port shown) in your browser

**The frontend will automatically connect to the Java backend API!**

The web interface features:
- **Modern dark theme** with orange fade effects
- **Interactive sections**: Search, Books, Users, Loans, Analytics
- **Real-time updates** and smooth animations
- **Responsive design** for different screen sizes
- **Full integration** with Java backend via REST API

#### Option 2: Web Frontend Only (Mock Data)

If you want to run just the frontend without the backend:

1. Navigate to the `web` directory
2. Open `index.html` with Live Server
3. The frontend will show an error message but you can still view the UI

**Note**: For full functionality, you need to run the API server (Option 1).

#### Option 3: Command-Line Interface

1. Compile the project:
   ```bash
   javac -d bin -sourcepath src src/App.java
   ```

2. Run the application:
   ```bash
   java -cp bin App
   ```

   Or use VS Code's built-in Java runner.

### Using the CLI

Upon starting, you'll see the main menu with the following options:

1. **Book Operations**
   - Add new books
   - List all books
   - List available books
   - View book details

2. **User Operations**
   - Add new users
   - List all users
   - View user details and loan history

3. **Loan Operations**
   - Borrow books (by ID or barcode)
   - Return books
   - View user loans
   - View overdue loans

4. **Search**
   - Search books using natural language queries

5. **Analytics Dashboard**
   - View library statistics and insights

6. **Exit**
   - Close the application

## Sample Data

The application comes pre-loaded with sample data:
- 4 sample books (Java Programming, Data Structures, Machine Learning, Database Systems)
- 3 sample users

You can start using the system immediately or add your own data.

## Architecture

### Design Patterns

- **Repository Pattern**: Abstract data access layer (currently in-memory, ready for database migration)
- **Service Layer**: Business logic separation
- **MVC-like Structure**: Clear separation of concerns

### Data Storage

Currently uses **in-memory storage** (HashMap-based repositories). This is suitable for MVP demonstration and can be easily replaced with:
- Database (MySQL, PostgreSQL, etc.)
- File-based storage (JSON, XML)
- Cloud storage solutions

## Future Enhancements

### Planned Features for Full Version

1. **AI-Powered Semantic Search**
   - Natural language processing for better search results
   - Context-aware recommendations

2. **Hybrid Recommendation Engine**
   - Content-based filtering
   - Collaborative filtering
   - User preference learning

3. **Advanced Analytics**
   - Usage patterns analysis
   - Predictive analytics
   - Collection optimization suggestions

4. **RFID/Barcode Integration**
   - Hardware integration for automated checkout
   - Inventory management

5. **Web Interface** ✅ (Fully Implemented)
   - Modern dark-themed web interface with orange fade effects
   - Interactive dashboard with real-time updates
   - Mobile-responsive design
   - RESTful API integration ✅ (Fully Connected)

6. **Database Integration**
   - Persistent data storage
   - Data backup and recovery

7. **Multi-user Support**
   - Role-based access control
   - Librarian vs. patron views

## Technical Details

### Key Classes

- **Book**: Represents library resources with metadata and availability tracking
- **User**: Library patrons with preferences and loan history
- **Loan**: Transaction records with due date tracking and fine calculation
- **LibraryService**: Core business logic orchestrating all operations
- **CLI**: Command-line interface for user interaction

### Loan Management

- Standard loan period: 14 days
- Fine calculation: $0.50 per day overdue
- Automatic availability updates
- Overdue tracking

## Contributing

This is an MVP project. Contributions and suggestions are welcome for:
- Code improvements
- Feature additions
- Bug fixes
- Documentation enhancements

## License

This project is part of the SmartLib AI development initiative.

## Contact

For questions or feedback about SmartLib AI MVP, please refer to the project documentation or development team.

---

**Note**: This MVP demonstrates core functionality. For production use, additional features like persistent storage, authentication, and advanced AI capabilities should be implemented.
