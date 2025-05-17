import java.sql.*;
import java.util.Scanner;

/*
 Note: Make sure here we are not using any build tools
 */
public class Demo {
    private static final String URL = "jdbc:postgresql://localhost:5432/Employee";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        ensureTableExists();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n🔹 Employee CRUD Operations 🔹");
                System.out.println("1. Insert Employee");
                System.out.println("2. View Employees");
                System.out.println("3. Update Employee Age");
                System.out.println("4. Delete Employee");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> insertEmployee(scanner);
                    case 2 -> viewEmployees();
                    case 3 -> updateEmployeeAge(scanner);
                    case 4 -> deleteEmployee(scanner);
                    case 5 -> {
                        System.out.println("🔴 Exiting program...");
                        return;
                    }
                    default -> System.out.println("⚠️ Invalid choice, try again!");
                }
            }
        }
    }

    private static void ensureTableExists() {
        String query = """
        CREATE TABLE IF NOT EXISTS public."EmployeeTable" (
            id SERIAL PRIMARY KEY,
            name TEXT NOT NULL,
            age INT NOT NULL
        );
    """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("✅ Checked: EmployeeTable exists or was created.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to create or verify table:");
            e.printStackTrace();
        }
    }


    // 1️⃣ INSERT: Now fetches generated ID
    private static void insertEmployee(Scanner scanner) {
        System.out.print("Enter Employee Name: ");
        String name = scanner.next();
        System.out.print("Enter Age: ");
        int age = scanner.nextInt();

        String query = "INSERT INTO public.\"EmployeeTable\" (name, age) VALUES (?, ?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Employee added with ID: " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2️⃣ SELECT: View employees with their auto-generated ID
    private static void viewEmployees() {
        String query = "SELECT * FROM public.\"EmployeeTable\"";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n📋 Employee List:");
            while (rs.next()) {
                System.out.println("👤 ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("name") +
                        " | Age: " + rs.getInt("age"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3️⃣ UPDATE: Using `name` instead of `id`
    private static void updateEmployeeAge(Scanner scanner) {
        System.out.print("Enter Employee Name to Update: ");
        String name = scanner.next();
        System.out.print("Enter New Age: ");
        int newAge = scanner.nextInt();

        String query = "UPDATE public.\"EmployeeTable\" SET age = ? WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, newAge);
            pstmt.setString(2, name);
            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("✅ Employee updated! Rows affected: " + rowsUpdated);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4️⃣ DELETE: Using `name` instead of `id`
    private static void deleteEmployee(Scanner scanner) {
        System.out.print("Enter Employee Name to Delete: ");
        String name = scanner.next();

        String query = "DELETE FROM public.\"EmployeeTable\" WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("✅ Employee deleted! Rows affected: " + rowsDeleted);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
