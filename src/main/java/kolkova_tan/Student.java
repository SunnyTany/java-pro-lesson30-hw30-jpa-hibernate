package kolkova_tan;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // Referencing the 'student' field in the Homework class. Cascade manages the persistence of dependent objects
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Homework> homeworks = new HashSet<>();

    // Пустой конструктор обязателен для HibernateAn empty constructor is required by Hibernate
    public Student() {}

    public Student(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Synchronized add/remove methods
    public void addHomework(final Homework homework) {
        this.homeworks.add(homework);
        homework.setStudent(this);
    }

    public void removeHomework(final Homework homework) {
        this.homeworks.remove(homework);
        homework.setStudent(null);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<Homework> getHomeworks() { return homeworks; }
    public void setHomeworks(Set<Homework> homeworks) { this.homeworks = homeworks; }

    // equals and hashCode by id field
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Student{" + "id=" + id + ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' + ", email='" + email + '\'' + '}';
    }
}