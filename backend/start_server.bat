@echo off
echo ============================================
echo.
echo Make sure XAMPP MySQL is running first!
echo.
echo Starting FastAPI server...
echo API Docs: http://127.0.0.1:8000/docs
echo.
.\venv\Scripts\python.exe -m uvicorn main:app --reload --host 0.0.0.0 --port 8000
pause