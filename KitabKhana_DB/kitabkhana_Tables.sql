CREATE SCHEMA IF NOT EXISTS kitabkhana;
SET SCHEMA 'kitabkhana';


CREATE TABLE Genre (
    name VARCHAR(100) PRIMARY KEY
);

CREATE TABLE Book (
    id SERIAL PRIMARY KEY,
    ISBN VARCHAR(20) NOT NULL,
    author VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL CHECK (state IN ('Available', 'Borrowed', 'Reserved')),
    FOREIGN KEY (genre) REFERENCES Genre(name)
);


CREATE TABLE BookGenre (
    ISBN VARCHAR(20) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    PRIMARY KEY (ISBN, genre),
    FOREIGN KEY (genre) REFERENCES Genre(name)
);

CREATE TABLE "User" (
    username VARCHAR(100) PRIMARY KEY,
    password TEXT NOT NULL,
    name VARCHAR(150) NOT NULL,
    phoneNumber VARCHAR(50),
    isLibrarian BOOLEAN NOT NULL DEFAULT FALSE,
    managedBy VARCHAR(100),
    FOREIGN KEY (managedBy) REFERENCES "User"(username) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT ck_user_no_self_manage CHECK (managedBy IS NULL OR managedBy <> username)
);

CREATE TABLE Loan (
    id SERIAL PRIMARY KEY,
    borrowDate DATE NOT NULL DEFAULT CURRENT_DATE,
    dueDate DATE NOT NULL,
    isReturned BOOLEAN NOT NULL DEFAULT FALSE,
    numberOfExtensions INT NOT NULL DEFAULT 0 CHECK (numberOfExtensions >= 0),
    username VARCHAR(100) NOT NULL,
    bookCopyId INT NOT NULL,
    FOREIGN KEY (username) REFERENCES "User"(username) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (bookCopyId) REFERENCES Book(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (dueDate >= borrowDate)
);

-- CREATE TABLE IF NOT EXISTS Reservation (
--     id SERIAL PRIMARY KEY,
--     numberInLine INT NOT NULL CHECK (numberInLine >= 1),
--     bookCopyId INT NOT NULL,
--     username VARCHAR(100) NOT NULL,
--     FOREIGN KEY (bookCopyId) REFERENCES Book(id) ON DELETE CASCADE ON UPDATE CASCADE,
--     FOREIGN KEY (username)   REFERENCES "User"(username) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT one_user_per_copy UNIQUE (bookCopyId, username),
--     CONSTRAINT one_position_per_copy UNIQUE (bookCopyId, numberInLine)
-- );

-- =========================
-- Sample seed data
-- =========================
-- Genres
INSERT INTO Genre(name) VALUES ('Programming'), ('Software Engineering')
ON CONFLICT DO NOTHING;

-- Books
INSERT INTO Book(ISBN, author, title, genre, state) VALUES
-- Clean Code (3 copies)
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Software Engineering', 'Available'),
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Software Engineering', 'Borrowed'),
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Software Engineering', 'Available'),

-- Clean Architecture (2 copies)
('9780134494166', 'Robert C. Martin', 'Clean Architecture', 'Software Engineering', 'Available'),
('9780134494166', 'Robert C. Martin', 'Clean Architecture', 'Software Engineering', 'Available'),

-- Pragmatic Programmer (2 copies)
('9780201616224', 'Andrew Hunt & David Thomas', 'The Pragmatic Programmer', 'Software Engineering', 'Available'),
('9780201616224', 'Andrew Hunt & David Thomas', 'The Pragmatic Programmer', 'Software Engineering', 'Borrowed'),

-- Effective Java (2 copies)
('9780134685991', 'Joshua Bloch', 'Effective Java', 'Programming', 'Available'),
('9780134685991', 'Joshua Bloch', 'Effective Java', 'Programming', 'Available'),

-- Head First Java (2 copies)
('9780596009205', 'Kathy Sierra & Bert Bates', 'Head First Java', 'Programming', 'Available'),
('9780596009205', 'Kathy Sierra & Bert Bates', 'Head First Java', 'Programming', 'Borrowed'),

-- Design Patterns (2 copies)
('9780201633610', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides',
 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Software Engineering', 'Available'),
('9780201633610', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides',
 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Software Engineering', 'Available'),

-- Refactoring (2 copies)
('9780321356680', 'Martin Fowler', 'Refactoring: Improving the Design of Existing Code', 'Software Engineering', 'Available'),
('9780321356680', 'Martin Fowler', 'Refactoring: Improving the Design of Existing Code', 'Software Engineering', 'Borrowed'),

-- Designing Data-Intensive Applications (2 copies)
('9781491903995', 'Martin Kleppmann', 'Designing Data-Intensive Applications', 'Software Engineering', 'Available'),
('9781491903995', 'Martin Kleppmann', 'Designing Data-Intensive Applications', 'Software Engineering', 'Borrowed'),

-- Single-copy books
('9781617294945', 'Craig Walls', 'Spring in Action', 'Programming', 'Available'),
('9780137081073', 'Kent Beck', 'Test-Driven Development: By Example', 'Software Engineering', 'Available'),
('9781492056812', 'Luciano Ramalho', 'Fluent Python', 'Programming', 'Available'),
('9780132354790', 'Michael Feathers', 'Working Effectively with Legacy Code', 'Software Engineering', 'Available')
ON CONFLICT DO NOTHING;

-- BookGenre
INSERT INTO BookGenre(ISBN, genre) VALUES
-- Clean Code
('9780132350884', 'Software Engineering'),

-- Clean Architecture
('9780134494166', 'Software Engineering'),

-- Pragmatic Programmer
('9780201616224', 'Software Engineering'),

-- Effective Java
('9780134685991', 'Programming'),

-- Head First Java
('9780596009205', 'Programming'),

-- Design Patterns
('9780201633610', 'Software Engineering'),

-- Refactoring
('9780321356680', 'Software Engineering'),

-- Designing Data-Intensive Applications
('9781491903995', 'Software Engineering'),

-- Spring in Action
('9781617294945', 'Programming'),

-- Test-Driven Development
('9780137081073', 'Software Engineering'),

-- Fluent Python
('9781492056812', 'Programming'),

-- Working Effectively with Legacy Code
('9780132354790', 'Software Engineering')
ON CONFLICT DO NOTHING;

-- User
INSERT INTO "User"(username, password, name, phoneNumber, isLibrarian) VALUES
('lib.admin',  '***hash***', 'Head Librarian', '+45-1111', TRUE),
('stud.alex',  '***hash***', 'Alex Student',   '+45-2222', FALSE)
ON CONFLICT DO NOTHING;