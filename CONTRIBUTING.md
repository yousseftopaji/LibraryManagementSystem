# 🚀 Contribution Workflow for Kitab Khana

## Overview
We use a **branch-based workflow** with **mandatory peer review** to ensure code quality and knowledge sharing across the team.

---

## 🔒 Main Branch Protection Rules

The `main` branch is **protected**. This means:

- ❌ **No direct commits** to `main` are allowed (not even by admins)
- ✅ **All changes** must go through a Pull Request (PR)
- ✅ Every PR requires **at least 1 approval** from another team member
- ✅ You **cannot approve your own PR**
- ✅ If you push new commits after approval, the PR must be **re-approved**
- ✅ Force pushes and branch deletions are **prohibited**

---

## 📝 Step-by-Step Workflow

### 1️⃣ Create a Feature Branch

Before starting work, create a new branch from `main`:

```bash
git checkout main
git pull origin main
git checkout -b feature/your-feature-name
```

**Branch naming conventions:**
- `feature/add-book-checkout` — for new features
- `bugfix/fix-login-error` — for bug fixes
- `refactor/improve-database-query` — for refactoring
- `docs/update-readme` — for documentation

---

### 2️⃣ Make Your Changes

Work on your branch and commit regularly:

```bash
git add .
git commit -m "Add book checkout functionality"
git push origin feature/your-feature-name
```

**Commit message tips:**
- Use clear, descriptive messages
- Start with a verb (Add, Fix, Update, Refactor)
- Example: `Fix null pointer exception in BookService`

---

### 3️⃣ Open a Pull Request (PR)

1. Go to the repository on GitHub
2. Click **"Compare & pull request"** (appears after you push)
3. Fill in the PR template:
   - **Title**: Clear summary (e.g., "Add book checkout feature")
   - **Description**: What changed and why
   - **Screenshots/Tests**: If applicable
4. Assign a **reviewer** (at least one teammate)
5. Click **"Create pull request"**

---

### 4️⃣ Code Review Process

**As the PR author:**
- ✅ Respond to feedback and questions
- ✅ Make requested changes by pushing new commits to your branch
- ✅ Resolve conversations when addressed
- ✅ Be open to suggestions — reviews help us all improve!

**As a reviewer:**
- ✅ Review the code thoroughly (logic, style, tests, edge cases)
- ✅ Leave constructive comments and suggestions
- ✅ Approve only when you're confident the code is ready
- ✅ Use "Request changes" if issues need fixing before merge

---

### 5️⃣ Address Feedback (if needed)

If changes are requested:

```bash
# Make the changes on your branch
git add .
git commit -m "Address review feedback: improve error handling"
git push origin feature/your-feature-name
```

⚠️ **Important:** New commits reset approvals! Your PR will need to be re-approved.

---

### 6️⃣ Merge the PR

Once approved:
1. Ensure all conversations are resolved
2. Click **"Merge pull request"**
3. Choose merge strategy:
   - **Create a merge commit** (default, keeps full history)
   - **Squash and merge** (combines commits into one — cleaner history)
   - **Rebase and merge** (linear history, advanced)
4. Confirm merge
5. **Delete the branch** after merging (keeps repo clean)

---

### 7️⃣ Pull Latest Changes

After your PR is merged (or someone else's):

```bash
git checkout main
git pull origin main
```

Always pull `main` before creating a new branch!

---

## 🚨 What If I'm Blocked?

**Problem:** "I can't push to main!"
- ✅ **Solution:** This is intentional! Create a branch and open a PR.

**Problem:** "My PR can't be merged without approval"
- ✅ **Solution:** Ask a teammate to review. Tag them with `@username` in a comment.

**Problem:** "I got new review comments after approval"
- ✅ **Solution:** Address them, push changes, and request re-approval.

**Problem:** "I need to make a hotfix urgently!"
- ✅ **Solution:** Still follow the process, but communicate urgency. A teammate can review quickly.

---

## 🎯 Best Practices

✅ **Keep PRs small and focused** (easier to review)  
✅ **Write descriptive PR titles and descriptions**  
✅ **Add tests when applicable** (especially for bug fixes)  
✅ **Review code promptly** (don't block teammates)  
✅ **Be respectful and constructive** in reviews  
✅ **Delete branches after merging** (keeps repo tidy)  
✅ **Pull main frequently** to avoid merge conflicts  

---

## 🛠️ Quick Reference Commands

```bash
# Start new work
git checkout main
git pull origin main
git checkout -b feature/my-feature

# Push your branch
git push origin feature/my-feature

# Update your branch with latest main (if needed)
git checkout main
git pull origin main
git checkout feature/my-feature
git merge main

# After PR is merged
git checkout main
git pull origin main
git branch -d feature/my-feature  # Delete local branch
```

---

**Happy coding! 🎉**