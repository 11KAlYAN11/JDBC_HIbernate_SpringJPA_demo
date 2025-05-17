import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        // Ensure table exists
        createTableIfNotExists(session);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n🔹 Choose an operation:");
            System.out.println("1. Insert Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> insertCustomer(session, scanner);
                case 2 -> viewCustomers(session);
                case 3 -> updateCustomer(session, scanner);
                case 4 -> deleteCustomer(session, scanner);
                case 5 -> {
                    running = false;
                    System.out.println("✅ Exiting...");
                }
                default -> System.out.println("⚠️ Invalid choice! Try again.");
            }
        }

        // Commit & Close
        tx.commit();
        session.close();
        factory.close();
        scanner.close();
    }

    private static void createTableIfNotExists(Session session) {
        // Note: make sure for "postgres" table is --- kalyantable ---  for "mysql"  -- employee ---
        /* for "mysql"
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS kalyantable (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                age INT NOT NULL
            )
        """; */
        // Bcz for mysql will use auto increment but for postgres serial 😎😎
        // for "postgres"
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS employeetable (
            id SERIAL PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            age INT NOT NULL
                       )
        
        """;

        // Note: if you don't need all this hectic simply use hibernate auto generation as mentioned in Customer.hbm.xml

        session.createSQLQuery(createTableSQL).executeUpdate();
        System.out.println("✅ Checked and ensured employee exists.");
    }

    private static void insertCustomer(Session session, Scanner scanner) {
        System.out.print("Enter customer name: ");
        scanner.nextLine(); // Consume leftover newline
        String name = scanner.nextLine();
        System.out.print("Enter customer age: ");
        int age = scanner.nextInt();

        Customer customer = new Customer(name, age);
        // If there is any issue with ID's
        // session.createNativeQuery("SELECT setval('kalyantable_id_seq', (SELECT MAX(id) FROM kalyantable))").executeUpdate();

        session.save(customer);
        System.out.println("✅ Customer Inserted: " + customer);
    }

    private static void viewCustomers(Session session) {
        List<Customer> customers = session.createQuery("FROM Customer", Customer.class).list();
        if (customers.isEmpty()) {
            System.out.println("⚠️ No customers found.");
        } else {
            customers.forEach(System.out::println);
        }
    }

    private static void updateCustomer(Session session, Scanner scanner) {
        System.out.print("Enter customer ID to update: ");
        int id = scanner.nextInt();
        Customer customer = session.get(Customer.class, id);

        if (customer != null) {
            System.out.print("Enter new age: ");
            int newAge = scanner.nextInt();
            customer.setAge(newAge);
            session.update(customer);
            System.out.println("✅ Customer Updated: " + customer);
        } else {
            System.out.println("⚠️ Customer not found.");
        }
    }

    private static void deleteCustomer(Session session, Scanner scanner) {
        System.out.print("Enter customer ID to delete: ");
        int id = scanner.nextInt();
        Customer customer = session.get(Customer.class, id);

        if (customer != null) {
            session.delete(customer);
            System.out.println("✅ Customer Deleted: " + customer);
        } else {
            System.out.println("⚠️ Customer not found.");
        }
    }
}
