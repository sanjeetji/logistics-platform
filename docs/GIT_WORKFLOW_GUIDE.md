# 🌿 Git & GitHub Workflow Guide

This document outlines the standard branching, testing, and CI/CD workflow for the Logistics Platform. It is designed to work perfectly for a **single developer** while enforcing best practices so that the transition to a **larger team** in the future is seamless.

---

## 1. The Strategy: Trunk-Based Development (GitHub Flow)

We use a simplified, highly effective branching strategy:
- **`main`**: The golden, always-deployable branch. You should **never** push code directly to `main`.
- **Feature Branches**: `feature/*`, `fix/*`, `hotfix/*`. All daily work happens here.

---

## 2. Step-by-Step Solo Developer Workflow

Even as a solo developer, following this PR-based workflow ensures your CI/CD pipeline runs correctly, your Docker registry isn't cluttered with broken builds, and your `main` branch is always stable.

### Step 1: Create a Feature Branch
Always start pulling the latest `main` before creating a new branch.

```bash
git checkout main
git pull origin main
git checkout -b feature/user-profile-update
```

### Step 2: Write Code & Test Locally
Write your code. Before pushing, ensure everything works locally:

```bash
# 1. Compile and run unit tests locally
mvn clean test

# 2. Run the platform locally to verify behavior
./docker/scripts/run-platform.sh build
./docker/scripts/run-platform.sh start

# 3. (Optional) Run integration tests
./docker/scripts/run-integration-test.sh
```

### Step 3: Commit and Push the Feature Branch
Once you are happy with the local results, commit and push your branch to GitHub.

```bash
git add .
git commit -m "feat: implement user profile update"
git push -u origin feature/user-profile-update
```
> **What happens in CI/CD now?**  
> GitHub Actions automatically triggers the `ci.yml` workflow. It compiles the code and runs all tests in the cloud to ensure no compilation errors were missed. It **does not** build a Docker image yet.

### Step 4: Create a Pull Request (PR)
Go to your repository on GitHub.
1. You will see a prompt: "feature/user-profile-update had recent pushes... **Compare & pull request**". Click it.
2. Provide a brief title and description.
3. Click **Create pull request**.

### Step 5: Wait for CI Checks to Pass
On the PR page, you will see the `ci.yml` workflow running as a "Check". 
- Wait for it to turn **Green (✅)**.
- If it fails (❌), check the logs, fix the bug locally, commit, and push to the same branch. The PR will update and re-test automatically.

### Step 6: Merge the PR
As a solo developer, you are your own reviewer!
1. Once all CI checks are green, click the **Merge pull request** button on GitHub.
2. Confirm the merge.
3. Delete the feature branch on GitHub (there is usually a button for this immediately after merging).

> **What happens in CI/CD now?**  
> Because code was merged into `main`, GitHub Actions triggers **both** `ci.yml` and `docker-build.yml`. 
> 1. It runs tests one final time.
> 2. It builds the official Docker image.
> 3. It publishes the image to GitHub Container Registry (GHCR) as `latest`.
> 4. It runs a Trivy security scan on the image.

### Step 7: Clean Up Locally
Go back to your terminal, switch to `main`, and delete your local feature branch.

```bash
git checkout main
git pull origin main
git branch -d feature/user-profile-update
```

You are now ready to start Step 1 again for the next feature!

---

## 3. GitHub Repository Settings (Required Configuration)

To enforce this workflow and prevent accidental direct pushes to `main`, you must configure Branch Protection Rules in GitHub.

1. Go to your repository on GitHub → **Settings** → **Branches**.
2. Click **Add branch protection rule**.
3. **Branch name pattern**: type `main`.
4. Check the following boxes:
   - ✅ **Require a pull request before merging** 
     - *Note: As a solo developer, you can uncheck "Require approvals" so you can merge your own PRs immediately, but keep the PR requirement enabled.*
   - ✅ **Require status checks to pass before merging**
     - Search for and select the `build` and `test` jobs from your `ci.yml` workflow so they are strictly required.
   - ✅ **Do not allow bypassing the above settings**
5. Click **Create**.

---

## 4. Why is this possible and best for a Solo Developer?

Yes, **it is absolutely possible and highly recommended** to use this PR workflow as a solo developer.

**Benefits for you right now:**
1. **Safety Net**: You will never accidentally break `main`. If you push broken code, the CI catches it in the PR before it touches the stable codebase.
2. **Cost & Space Efficiency**: Docker images are heavy. By only building images when code is merged to `main`, you save massive amounts of GHCR storage limit quotas and GitHub Actions minutes.
3. **Context Switching**: You can have multiple PRs open for different features and switch between them without them interfering with each other.

**Benefits for the future (when a team joins):**
1. **Zero Friction Transition**: When a second developer joins, they just follow this exact guide.
2. **Ready for Code Review**: You simply go into Branch Protection Settings and check the box that says "Require 1 approval". Now, developers must review each other's PRs before they can be merged. The entire pipeline is already built to support this natively.
