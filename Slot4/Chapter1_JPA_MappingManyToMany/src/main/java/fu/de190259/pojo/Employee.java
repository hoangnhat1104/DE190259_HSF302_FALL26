package fu.de190259.pojo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * TODO 5.1 — Entity Employee (tái sử dụng từ bài OneToMany, bỏ quan hệ Department)
 * Đại diện cho bảng "employees" trong DB.
 *
 * Dùng Set<Project> thay vì List<Project>:
 *   - Tránh trùng lặp: cùng 1 project không thể xuất hiện 2 lần trong Set của 1 employee
 *   - Hibernate không sinh ra Cartesian product như khi dùng List với nhiều JOIN FETCH
 *   - Yêu cầu equals()/hashCode() đúng ở Project để Set hoạt động chính xác
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

    // TODO 5.2 — Owning side của quan hệ N–N (sẽ hoàn thiện ở TODO 5.2)
    // Khởi tạo sẵn HashSet để tránh NullPointerException khi gọi .add()
    private Set<Project> projects = new HashSet<>();

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

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    /**
     * TODO 5.4 — equals() dựa trên email (business key), KHÔNG dùng id.
     *
     * Lý do không dùng id:
     *   - Trước khi persist(), id = null → 2 object khác nhau đều có id = null
     *     → equals() trả về true sai → vi phạm hợp đồng equals/hashCode.
     *   - Sau persist(), id được gán tự động → hashCode() thay đổi
     *     → entity bị "mất" trong HashSet (vì bucket đã thay đổi).
     *   - Email là unique + not null → đảm bảo tính nhất quán trong toàn bộ vòng đời entity,
     *     kể cả khi entity chưa được lưu xuống DB.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
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
