import pymysql
import os
from dotenv import load_dotenv

load_dotenv()

DB_HOST = os.getenv("DB_HOST", "127.0.0.1")
DB_USER = os.getenv("DB_USER", "root")
DB_PASS = os.getenv("DB_PASS", "")
DB_NAME = os.getenv("DB_NAME", "skill_exchange")

def update_db():
    try:
        conn = pymysql.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASS,
            database=DB_NAME
        )
        cursor = conn.cursor()
        
        print(f"Connected to {DB_NAME} on {DB_HOST}")
        
        # Check if columns exist
        cursor.execute("SHOW COLUMNS FROM users")
        columns = [col[0] for col in cursor.fetchall()]
        
        if "gender" not in columns:
            print("Adding 'gender' column...")
            cursor.execute("ALTER TABLE users ADD COLUMN gender VARCHAR(20) DEFAULT NULL")
        
        if "phone" not in columns:
            print("Adding 'phone' column...")
            cursor.execute("ALTER TABLE users ADD COLUMN phone VARCHAR(20) DEFAULT NULL")
            
        conn.commit()
        print("Database schema updated successfully.")
        
    except Exception as e:
        print(f"Error: {e}")
    finally:
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    update_db()
