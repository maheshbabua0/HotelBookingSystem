# Hotel Booking System (Java + MySQL)

## What is included
- Java Swing GUI application (source in `src/`)
- SQL dump to create database and tables (`sql/hotel_db.sql`)
- Instructions to run

## Prerequisites
- Java JDK 8 or newer
- MySQL server
- `mysql-connector-j` JDBC driver (add jar to classpath)

## Setup
1. Import or run `sql/hotel_db.sql` in your MySQL (e.g., using MySQL Workbench).
2. Update `src/DBConnection.java` with your MySQL username/password.
3. Add `mysql-connector-j.jar` to your project's classpath.
4. Compile all `.java` files in `src/`:
   ```
   javac -cp .:path/to/mysql-connector-j.jar src/*.java
   ```
5. Run the application:
   ```
   java -cp .:path/to/mysql-connector-j.jar src.LoginFrame
   ```
(On Windows replace `:` with `;` in classpath)

## Default logins
- admin / admin123 (role: admin)
- reception / rec123 (role: receptionist)

## Notes & Suggestions
- This is a simple educational example. For production:
  - Hash passwords (do not store plaintext).
  - Use connection pooling.
  - Add input validation and error handling.
  - Move to MVC structure and consider using frameworks (Spring Boot, Hibernate).
