"""
init_db.py — Force create all MySQL tables for Skill Exchange AI (No Emojis for Windows Console)
"""
import sys
import os

# Add current directory to path so we can import models
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from database import engine, Base
import models.user
import models.project
import models.chat
import models.funding
import models.notification

print("Connecting to database and creating tables...")

try:
    Base.metadata.create_all(bind=engine)
    print("SUCCESS: All tables created successfully in 'skillexchange' database.")
    
    # List tables to confirm
    from sqlalchemy import inspect
    inspector = inspect(engine)
    tables = inspector.get_table_names()
    print(f"Current tables in database: {', '.join(tables)}")
    
except Exception as e:
    print(f"ERROR: Failed to create tables: {e}")
