-- Seed data for testing the Book-Genre relationship

-- Insert Users
INSERT INTO [User] (username, passwordhash, role, name, phonenumber, email)  VALUES
('john_doe', 'hashed_password_1', 'User', 'John Doe', '123-456-7890', 'john@g.c'),
('jane_smith', 'hashed_password_2', 'Admin', 'Jane Smith', '987-654-3210', 'jane@g.c');