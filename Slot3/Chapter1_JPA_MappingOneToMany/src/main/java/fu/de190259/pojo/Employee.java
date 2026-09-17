package fu.de190259.pojo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TODO 2.1 — Entity Employee
 * Đại diện cho bảng "employees" trong DB.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "salary")
    private BigDecimal salary;

    // LocalDate — JPA 2.2+ map thẳng, không cần @Temporal
    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // Dùng STRING để lưu "MALE"/"FEMALE"/"OTHER", không lưu số thứ tự
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    // boolean nguyên thủy — không cho phép null
    @Column(name = "active")
    private boolean active = true;

    // Constructor không tham số — bắt buộc với JPA
    public Employee() {
    }

    public Employee(String email, String fullName, Gender gender,
                    BigDecimal salary, LocalDate hireDate) {
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.salary = salary;
        this.hireDate = hireDate;
        this.active = true;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Employee{id=" + id
                + ", fullName='" + fullName + "'"
                + ", email='" + email + "'"
                + ", gender=" + gender
                + ", salary=" + salary
                + ", hireDate=" + hireDate
                + ", active=" + active + "}";
    }
}
