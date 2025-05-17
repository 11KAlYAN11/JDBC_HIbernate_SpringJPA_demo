import javax.persistence.*;
//import jakarta.persistence.*;

@Entity
@Table(name = "employeetable")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private int id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "age", nullable = false)
    private int age;

    // Constructors
    public Customer() {}

    public Customer(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }


    @Override
    public String toString() {
        return "Customer { id=" + id + ", name='" + name + "', age=" + age + " }";
    }
}
