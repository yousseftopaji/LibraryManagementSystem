-- =============================================
-- VIEW BOOKS WITH LOAN STATUS
-- =============================================

-- Query 1: All books with their current loan status
-- Shows if a book is on loan, who has it, and when it's due
SELECT
    b.id AS book_id,
    b.ISBN,
    b.title,
    b.author,
    b.state,
    CASE
        WHEN l.id IS NOT NULL THEN 'ON LOAN'
        ELSE 'AVAILABLE'
    END AS loan_status,
    l.user_id AS borrowed_by,
    u.name AS borrower_name,
    l.loan_date,
    l.due_date,
    CASE
        WHEN l.due_date < CURRENT_TIMESTAMP AND l.status = 'ACTIVE' THEN 'OVERDUE'
        WHEN l.status = 'ACTIVE' THEN 'ACTIVE'
        ELSE 'AVAILABLE'
    END AS current_status
FROM kitabkhana."Book" b
LEFT JOIN kitabkhana."Loan" l ON b.id = l.book_id AND l.status = 'ACTIVE'
LEFT JOIN kitabkhana."User" u ON l.user_id = u.username
ORDER BY b.title;

-- =============================================
-- Query 2: Only books currently on loan
-- =============================================
SELECT
    b.id AS book_id,
    b.ISBN,
    b.title,
    b.author,
    b.state,
    l.user_id AS borrowed_by,
    u.name AS borrower_name,
    l.loan_date,
    l.due_date,
    EXTRACT(DAY FROM (l.due_date - CURRENT_TIMESTAMP)) AS days_until_due,
    CASE
        WHEN l.due_date < CURRENT_TIMESTAMP THEN 'OVERDUE'
        WHEN EXTRACT(DAY FROM (l.due_date - CURRENT_TIMESTAMP)) <= 3 THEN 'DUE SOON'
        ELSE 'ACTIVE'
    END AS loan_status
FROM kitabkhana."Book" b
JOIN kitabkhana."Loan" l ON b.id = l.book_id AND l.status = 'ACTIVE'
JOIN kitabkhana."User" u ON l.user_id = u.username
ORDER BY l.due_date ASC;

-- =============================================
-- Query 3: Books available for loan (not currently borrowed)
-- =============================================
SELECT
    b.id AS book_id,
    b.ISBN,
    b.title,
    b.author,
    b.state
FROM kitabkhana."Book" b
WHERE NOT EXISTS (
    SELECT 1
    FROM kitabkhana."Loan" l
    WHERE l.book_id = b.id
    AND l.status = 'ACTIVE'
)
ORDER BY b.title;

-- =============================================
-- Query 4: Summary statistics
-- =============================================
SELECT
    COUNT(*) AS total_books,
    COUNT(CASE WHEN l.id IS NOT NULL THEN 1 END) AS books_on_loan,
    COUNT(CASE WHEN l.id IS NULL THEN 1 END) AS books_available,
    COUNT(CASE WHEN l.due_date < CURRENT_TIMESTAMP AND l.status = 'ACTIVE' THEN 1 END) AS overdue_loans
FROM kitabkhana."Book" b
LEFT JOIN kitabkhana."Loan" l ON b.id = l.book_id AND l.status = 'ACTIVE';

-- =============================================
-- Query 5: Loans by user
-- =============================================
SELECT
    u.username,
    u.name,
    COUNT(l.id) AS active_loans,
    STRING_AGG(b.title, ', ') AS borrowed_books
FROM kitabkhana."User" u
LEFT JOIN kitabkhana."Loan" l ON u.username = l.user_id AND l.status = 'ACTIVE'
LEFT JOIN kitabkhana."Book" b ON l.book_id = b.id
GROUP BY u.username, u.name
HAVING COUNT(l.id) > 0
ORDER BY active_loans DESC;

-- =============================================
-- Query 6: Overdue loans (IMPORTANT!)
-- =============================================
SELECT
    l.id AS loan_id,
    b.id AS book_id,
    b.ISBN,
    b.title,
    b.author,
    l.user_id,
    u.name AS borrower_name,
    u.phoneNumber,
    l.loan_date,
    l.due_date,
    EXTRACT(DAY FROM (CURRENT_TIMESTAMP - l.due_date)) AS days_overdue
FROM kitabkhana."Loan" l
JOIN kitabkhana."Book" b ON l.book_id = b.id
JOIN kitabkhana."User" u ON l.user_id = u.username
WHERE l.status = 'ACTIVE'
  AND l.due_date < CURRENT_TIMESTAMP
ORDER BY days_overdue DESC;

-- =============================================
-- Query 7: Detailed view - Books with ALL loan history
-- =============================================
SELECT
    b.id AS book_id,
    b.ISBN,
    b.title,
    b.author,
    b.state,
    l.id AS loan_id,
    l.user_id,
    u.name AS borrower_name,
    l.loan_date,
    l.due_date,
    l.return_date,
    l.status,
    CASE
        WHEN l.status = 'RETURNED' THEN 'Returned'
        WHEN l.status = 'ACTIVE' AND l.due_date < CURRENT_TIMESTAMP THEN 'Overdue'
        WHEN l.status = 'ACTIVE' THEN 'On Loan'
        ELSE 'Available'
    END AS display_status
FROM kitabkhana."Book" b
LEFT JOIN kitabkhana."Loan" l ON b.id = l.book_id
LEFT JOIN kitabkhana."User" u ON l.user_id = u.username
ORDER BY b.title, l.loan_date DESC;

