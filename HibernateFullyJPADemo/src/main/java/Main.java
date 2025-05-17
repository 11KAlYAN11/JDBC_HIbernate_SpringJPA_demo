import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Here for next line we are mentioning persistence-unit name as mentioned in persistence.xml (postgres / mysql pu)
        // Note: just by switching the persistence-unit you can use the DB (mySql, postgresSQL) u wish for
        // EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresSQL_PU");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MySQL_PU");
        EntityManager em = emf.createEntityManager();

        // Ensure table exists
        createTableIfNotExists(em);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n\ud83d\udd39 Choose an operation:");
            System.out.println("1. Insert Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> insertCustomer(em, scanner);
                case 2 -> viewCustomers(em);
                case 3 -> updateCustomer(em, scanner);
                case 4 -> deleteCustomer(em, scanner);
                case 5 -> {
                    running = false;
                    System.out.println("✅ Exiting...");
                }
                default -> System.out.println("⚠️ Invalid choice! Try again.");
            }
        }

        em.close();
        emf.close();
        scanner.close();
    }

    private static void createTableIfNotExists(EntityManager em) {
        // for "mysql" -> employee Table & pavan DB
        // As of now it is not a matter I think bcz the persistence is creating new table each time
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS employee (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                age INT NOT NULL
            )
        """;

        // Bcz for mysql will use auto increment but for postgres serial 😎😎
        // for "postgres"
        /*String createTableSQL = """
            CREATE TABLE IF NOT EXISTS employeetable (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                age INT NOT NULL
            )
        """;*/

        // Note: if you don't need all this hectic simply use JPA auto generation as mentioned in persistence.xml
        em.getTransaction().begin();
        em.createNativeQuery(createTableSQL).executeUpdate();
        em.getTransaction().commit();
        System.out.println("✅ Checked and ensured employee table exists.");
    }

    private static void insertCustomer(EntityManager em, Scanner scanner) {
        System.out.print("Enter customer name: ");
        scanner.nextLine(); // Consume leftover newline
        String name = scanner.nextLine();
        System.out.print("Enter customer age: ");
        int age = scanner.nextInt();

        Customer customer = new Customer(name, age);
        em.getTransaction().begin();
        em.persist(customer);
        em.getTransaction().commit();

        System.out.println("✅ Customer Inserted: " + customer);
    }

    private static void viewCustomers(EntityManager em) {
        List<Customer> customers = em.createQuery("FROM Customer", Customer.class).getResultList();
        if (customers.isEmpty()) {
            System.out.println("⚠️ No customers found.");
        } else {
            customers.forEach(System.out::println);
        }
    }

    private static void updateCustomer(EntityManager em, Scanner scanner) {
        System.out.print("Enter customer ID to update: ");
        int id = scanner.nextInt();

        Customer customer = em.find(Customer.class, id);
        if (customer != null) {
            System.out.print("Enter new age: ");
            int newAge = scanner.nextInt();
            em.getTransaction().begin();
            customer.setAge(newAge);
            em.getTransaction().commit();
            System.out.println("✅ Customer Updated: " + customer);
        } else {
            System.out.println("⚠️ Customer not found.");
        }
    }

    private static void deleteCustomer(EntityManager em, Scanner scanner) {
        System.out.print("Enter customer ID to delete: ");
        int id = scanner.nextInt();

        Customer customer = em.find(Customer.class, id);
        if (customer != null) {
            em.getTransaction().begin();
            em.remove(customer);
            em.getTransaction().commit();
            System.out.println("✅ Customer Deleted: " + customer);
        } else {
            System.out.println("⚠️ Customer not found.");
        }
    }
}
