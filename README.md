# Skill Exchange AI Application

This project is a full-stack platform for skill sharing, project collaboration, and investment. It consists of a **FastAPI Python Backend** and a **Native Android Application**.

## 🛠 Prerequisites

Before sharing or running this project, ensure you have the following installed:
1. **Python 3.10+**
2. **XAMPP** (for MySQL Database)
3. **Android Studio** (to run the mobile app)

---

## 🚀 Setup Instructions

### 1. Database Setup (MySQL)
1. Open the **XAMPP Control Panel** and start **MySQL**.
2. Go to **phpMyAdmin** (`http://localhost/phpmyadmin`).
3. Create a new database named `skillexchange`.
4. Import the schema file:
   - Location: `backend/schema.sql`
   - Use the "Import" tab in phpMyAdmin.

### 2. Backend Setup & Installation (Python)

1. **Open your terminal / shell** and navigate to the `backend` directory:
   ```powershell
   cd backend
   ```

2. **Create a virtual environment** to isolate dependencies:
   ```powershell
   python -m venv venv
   ```

3. **Activate the virtual environment**:
   *   **Windows (PowerShell):**
       ```powershell
       .\venv\Scripts\Activate.ps1
       ```
   *   **Windows (Command Prompt):**
       ```cmd
       .\venv\Scripts\activate.bat
       ```
   *   **Linux / macOS:**
       ```bash
       source venv/bin/activate
       ```

4. **Install all required dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

5. **Configure environment settings**:
   Verify or update the database credentials in the `.env` file located inside the `backend/` folder to match your local setup:
   ```env
   DB_HOST=127.0.0.1
   DB_PORT=3306
   DB_USER=root
   DB_PASSWORD=
   DB_NAME=skillexchange
   ```

6. **Initialize the Database Schema**:
   With your virtual environment active, run the initialization and migration scripts to automatically create and setup all tables inside your MySQL `skillexchange` database:
   ```bash
   python init_db.py
   python migrate_db.py
   ```

### 3. Android App Setup
1. Open the `Android app` folder in **Android Studio**.
2. Wait for Gradle to sync.
3. Update the **API Base URL** if your backend is running on a different IP (usually found in a `Constants` or `Network` file in the Android project).

---

## 🏃 How to Run

### Start the Backend Server

You can start the FastAPI backend server using either of the following methods:

#### Option A: Quick-Start Batch (Windows)
Double-click the **`start_server.bat`** file located in the `backend` folder.

#### Option B: Manual Command (All Platforms)
Open a terminal in the `backend` directory, activate the virtual environment, and run:
```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

Once started successfully, the backend will be accessible at:
*   **Interactive API documentation (Swagger UI):** `http://localhost:8000/docs`
*   **Alternative API documentation (ReDoc):** `http://localhost:8000/redoc`

### Run the Android App
1. Connect an Android device or start an emulator.
2. Press **Run** (Green Play Button) in Android Studio.

---

## 📦 How to Share

When sharing this project as a ZIP file, **EXCLUDE** the following folders to keep the file size small and avoid errors:
- `backend/venv/` (The recipient must create their own)
- `backend/__pycache__/`
- `Android app/.gradle/`
- `Android app/build/`
- `Android app/app/build/`

**Note:** Always include the `requirements.txt` and `schema.sql` files as they are essential for setup.
