-- Seed data for testing the Book-Genre relationship

-- Insert Genres
INSERT INTO Genre (Name) VALUES ('Programming');
INSERT INTO Genre (Name) VALUES ('Software Engineering');
INSERT INTO Genre (Name) VALUES ('Fiction');
INSERT INTO Genre (Name) VALUES ('Science');

-- Insert Books
INSERT INTO Book (ISBN, Author, Title, State) VALUES 
('9780132350884', 'Robert C. Martin', 'Clean Code', 'Available'),
('9780134494166', 'Robert C. Martin', 'Clean Architecture', 'Available'),
('9780201616224', 'Andrew Hunt & David Thomas', 'The Pragmatic Programmer', 'Available'),
('9780134685991', 'Joshua Bloch', 'Effective Java', 'Borrowed');

-- Insert BookGenre relationships (using BookId and GenreName)
-- Clean Code is Programming and Software Engineering
INSERT INTO BookGenre (BookId, GenreName) VALUES (1, 'Programming');
INSERT INTO BookGenre (BookId, GenreName) VALUES (1, 'Software Engineering');

-- Clean Architecture is Software Engineering
INSERT INTO BookGenre (BookId, GenreName) VALUES (2, 'Software Engineering');

-- The Pragmatic Programmer is Programming and Software Engineering
INSERT INTO BookGenre (BookId, GenreName) VALUES (3, 'Programming');
INSERT INTO BookGenre (BookId, GenreName) VALUES (3, 'Software Engineering');

-- Effective Java is Programming
INSERT INTO BookGenre (BookId, GenreName) VALUES (4, 'Programming');

