import pymysql
import os
from dotenv import load_dotenv

# Load .env
load_dotenv()

host = os.getenv("DB_HOST", "127.0.0.1")
user = os.getenv("DB_USER", "root")
password = os.getenv("DB_PASSWORD", "")
database = os.getenv("DB_NAME", "skillexchange")

try:
    connection = pymysql.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    with connection.cursor() as cursor:
        # Check if column exists
        cursor.execute("SHOW COLUMNS FROM users LIKE 'profile_picture'")
        result = cursor.fetchone()
        
        if not result:
            print("Adding 'profile_picture' column to 'users' table...")
            cursor.execute("ALTER TABLE users ADD COLUMN profile_picture VARCHAR(500) NULL AFTER phone")
            connection.commit()
            print("Successfully added 'profile_picture' column!")
        else:
            print("Column 'profile_picture' already exists.")
            
except Exception as e:
    print(f"Error: {e}")
finally:
    if 'connection' in locals():
        connection.close()
