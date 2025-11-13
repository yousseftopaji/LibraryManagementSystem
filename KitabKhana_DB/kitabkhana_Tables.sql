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
    state VARCHAR(50) NOT NULL CHECK (state IN ('Available', 'Borrowed', 'Reserved'))
);

CREATE TABLE BookGenre (
    ISBN VARCHAR(20),
    genre VARCHAR(100),
    PRIMARY KEY (ISBN, genre),
    FOREIGN KEY (ISBN) REFERENCES Book(ISBN) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (genre) REFERENCES Genre(name) ON DELETE CASCADE ON UPDATE CASCADE
);

-- CREATE TABLE BookCopy (
--     id SERIAL PRIMARY KEY,
--     bookId INT NOT NULL,
--     copyNumber INT NOT NULL,
--     state VARCHAR(50) NOT NULL CHECK (state IN ('Available','Borrowed','Reserved')),
--     FOREIGN KEY (bookId) REFERENCES Book(id) ON DELETE CASCADE ON UPDATE CASCADE,
--     CONSTRAINT uq_book_copy_per_title UNIQUE (bookId, copyNumber)
-- );

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

-- CREATE TABLE Loan (
--     id SERIAL PRIMARY KEY,
--     borrowDate DATE NOT NULL DEFAULT CURRENT_DATE,
--     dueDate DATE NOT NULL,
--     isReturned BOOLEAN NOT NULL DEFAULT FALSE,
--     numberOfExtensions INT NOT NULL DEFAULT 0 CHECK (numberOfExtensions >= 0),
--     username VARCHAR(100) NOT NULL,
--     bookCopyId INT NOT NULL,
--     FOREIGN KEY (username) REFERENCES "User"(username) ON DELETE RESTRICT ON UPDATE CASCADE,
--     FOREIGN KEY (bookCopyId) REFERENCES Book(id) ON DELETE RESTRICT ON UPDATE CASCADE,
--     CHECK (dueDate >= borrowDate)
-- );

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

--DELETE FROM borrow_book.Book;
-- DELETE FROM borrow_book.BookGenre;

--DROP TABLE IF EXISTS borrow_book.Book CASCADE ;
--DROP TABLE IF EXISTS borrow_book.BookCopy CASCADE ;

--Alter table borrow_book.book ALTER column state set Not Null;

-- =========================
-- Sample seed data
-- =========================
-- Genres
INSERT INTO Genre(name) VALUES ('Programming'), ('Software Engineering')
ON CONFLICT DO NOTHING;

-- Books
INSERT INTO Book(ISBN, author, title, state) VALUES
-- Clean Code (3 copies)
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Available'),
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Borrowed'),
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Available'),

-- Clean Architecture (2 copies)
('9780134494166', 'Robert C. Martin', 'Clean Architecture', 'Available'),
('9780134494166', 'Robert C. Martin', 'Clean Architecture', 'Available'),

-- Pragmatic Programmer (2 copies)
('9780201616224', 'Andrew Hunt & David Thomas', 'The Pragmatic Programmer', 'Available'),
('9780201616224', 'Andrew Hunt & David Thomas', 'The Pragmatic Programmer', 'Borrowed'),

-- Effective Java (2 copies)
('9780134685991', 'Joshua Bloch', 'Effective Java', 'Available'),
('9780134685991', 'Joshua Bloch', 'Effective Java', 'Available'),

-- Head First Java (2 copies)
('9780596009205', 'Kathy Sierra & Bert Bates', 'Head First Java', 'Available'),
('9780596009205', 'Kathy Sierra & Bert Bates', 'Head First Java', 'Borrowed'),

-- Design Patterns (2 copies)
('9780201633610', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Available'),
('9780201633610', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Available'),

-- Refactoring (2 copies)
('9780321356680', 'Martin Fowler', 'Refactoring: Improving the Design of Existing Code', 'Available'),
('9780321356680', 'Martin Fowler', 'Refactoring: Improving the Design of Existing Code', 'Borrowed'),

-- Designing Data-Intensive Applications (2 copies)
('9781491903995', 'Martin Kleppmann', 'Designing Data-Intensive Applications', 'Available'),
('9781491903995', 'Martin Kleppmann', 'Designing Data-Intensive Applications', 'Borrowed'),

-- Single-copy books
('9781617294945', 'Craig Walls', 'Spring in Action', 'Available'),
('9780137081073', 'Kent Beck', 'Test-Driven Development: By Example', 'Available'),
('9781492056812', 'Luciano Ramalho', 'Fluent Python', 'Available'),
('9780132354790', 'Michael Feathers', 'Working Effectively with Legacy Code', 'Available');

ON CONFLICT DO NOTHING;

-- BookGenre
INSERT INTO BookGenre(ISBN, genre) VALUES
('9780132350884', 'Programming'),
('9780201616224', 'Software Engineering')
ON CONFLICT DO NOTHING;

-- BookCopy
-- INSERT INTO BookCopy(bookId, copyNumber, state)
-- SELECT b.id, 1, 'Available' FROM Book b WHERE b.ISBN = '9780132350884'
-- ON CONFLICT DO NOTHING;
--
-- INSERT INTO BookCopy(bookId, copyNumber, state)
-- SELECT b.id, 2, 'Borrowed'  FROM Book b WHERE b.ISBN = '9780132350884'
-- ON CONFLICT DO NOTHING;
--
-- INSERT INTO BookCopy(bookId, copyNumber, state)
-- SELECT b.id, 1, 'Reserved'  FROM Book b WHERE b.ISBN = '9780201616224'
-- ON CONFLICT DO NOTHING;

-- User
INSERT INTO "User"(username, password, name, phoneNumber, isLibrarian) VALUES
('lib.admin',  '***hash***', 'Head Librarian', '+45-1111', TRUE),
('stud.alex',  '***hash***', 'Alex Student',   '+45-2222', FALSE)
ON CONFLICT DO NOTHING;

-- Loan (Alex borrows Clean Code copy #2)
-- INSERT INTO Loan(dueDate, username, bookCopyId)
-- SELECT CURRENT_DATE + INTERVAL '30 days', 'stud.alex', bc.id
-- FROM BookCopy bc
-- JOIN Book b ON b.id = bc.bookId
-- WHERE b.ISBN = '9780132350884' AND bc.copyNumber = 2
-- ON CONFLICT DO NOTHING;
--
-- -- Reservation (Alex reserves Pragmatic copy #1, queue pos = 1)
-- INSERT INTO Reservation(numberInLine, bookCopyId, username)
-- SELECT 1, bc.id, 'stud.alex'
-- FROM BookCopy bc
-- JOIN Book b ON b.id = bc.bookId
-- WHERE b.ISBN = '9780201616224' AND bc.copyNumber = 1
-- ON CONFLICT DO NOTHING;

SELECT * from book;