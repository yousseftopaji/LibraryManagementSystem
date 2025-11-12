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

CREATE TABLE Loan (
    id SERIAL PRIMARY KEY,
    book_id INT NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    loan_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'RETURNED', 'OVERDUE')),
    FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES "User"(username) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (due_date >= loan_date)
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
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Available'),
('9780201616224', 'Andrew Hunt', 'The Pragmatic Programmer','Borrowed'),
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Available')
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

-- Sample Loans (Alex borrows "The Pragmatic Programmer")
INSERT INTO Loan(book_id, user_id, loan_date, due_date, status)
SELECT b.id, 'stud.alex', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days', 'ACTIVE'
FROM Book b WHERE b.ISBN = '9780201616224' AND b.state = 'Borrowed'
ON CONFLICT DO NOTHING;

-- =========================
-- Useful Queries
-- =========================

-- View all books
SELECT * FROM Book;

-- View all active loans with book details
SELECT
    l.id AS loan_id,
    b.id AS book_id,
    b.ISBN,
    b.title,
    b.author,
    b.state,
    l.user_id,
    u.name AS borrower_name,
    l.loan_date,
    l.due_date,
    l.status,
    CASE
        WHEN l.due_date < CURRENT_TIMESTAMP AND l.status = 'ACTIVE' THEN 'OVERDUE'
        ELSE l.status
    END AS current_status
FROM Loan l
JOIN Book b ON l.book_id = b.id
JOIN "User" u ON l.user_id = u.username
WHERE l.status = 'ACTIVE'
ORDER BY l.loan_date DESC;

-- View books that are currently on loan
SELECT
    b.id,
    b.ISBN,
    b.title,
    b.author,
    b.state,
    l.user_id AS borrowed_by,
    u.name AS borrower_name,
    l.loan_date,
    l.due_date
FROM Book b
JOIN Loan l ON b.id = l.book_id AND l.status = 'ACTIVE'
JOIN "User" u ON l.user_id = u.username
ORDER BY b.title;

-- View books that are available (not on loan)
SELECT
    b.id,
    b.ISBN,
    b.title,
    b.author,
    b.state
FROM Book b
WHERE b.state = 'Available'
  AND NOT EXISTS (
    SELECT 1 FROM Loan l
    WHERE l.book_id = b.id AND l.status = 'ACTIVE'
  )
ORDER BY b.title;
