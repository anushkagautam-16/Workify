# Workify
def save_to_db(df, table_name):
    """Save DataFrame to SQL Server database, dynamically adding missing columns."""
    try:
        conn = pyodbc.connect(
            Driver='{ODBC Driver 17 for SQL Server}',
            Server='eueort',
            Database='inm',
            Trusted_Connection='yes'
        )
        cursor = conn.cursor()

        # Check if table exists
        cursor.execute("""
            SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES 
            WHERE TABLE_NAME = ?
        """, (table_name,))
        table_exists = cursor.fetchone()[0] == 1

        if table_exists:
            print(f"Table '{table_name}' exists. Checking for missing columns...")

            # Get existing columns
            cursor.execute("""
                SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS 
                WHERE TABLE_NAME = ?
            """, (table_name,))
            existing_columns = set([row[0] for row in cursor.fetchall()])

            # Find missing columns
            for col in df.columns:
                if col not in existing_columns:
                    alter_sql = f"ALTER TABLE [{table_name}] ADD [{col}] NVARCHAR(MAX)"
                    cursor.execute(alter_sql)
                    print(f"Added missing column: {col}")
        else:
            # Table doesn't exist: create it
            columns = [f'[{col}] NVARCHAR(MAX)' for col in df.columns]
            create_table_sql = f"CREATE TABLE [{table_name}] ({','.join(columns)})"
            cursor.execute(create_table_sql)
            print(f"Table '{table_name}' created.")

        # Insert data
        for _, row in df.iterrows():
            placeholders = ','.join(['?'] * len(row))
            column_names = ','.join([f'[{col}]' for col in df.columns])
            insert_sql = f"INSERT INTO [{table_name}] ({column_names}) VALUES ({placeholders})"
            cursor.execute(insert_sql, tuple(row.astype(str)))

        conn.commit()
        print(f"Successfully saved {len(df)} rows to table '{table_name}'")

    except Exception as e:
        print(f"Database error: {str(e)}")
    finally:
        if 'conn' in locals():
            conn.close()
