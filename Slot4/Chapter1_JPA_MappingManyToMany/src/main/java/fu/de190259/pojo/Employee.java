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

    // TODO 5.2 — Owning side của quan hệ N–N
    //
    // Tại sao Employee là owning side?
    //   - Owning side là bên giữ @JoinTable — tức là bên "biết" về bảng trung gian.
    //   - Employee được chọn vì logic nghiệp vụ: nhân viên được phân công vào project,
    //     không phải project tự kéo nhân viên vào.
    //
    // @JoinTable:
    //   - name = "employee_project": tên bảng trung gian Hibernate sẽ tạo
    //   - joinColumns: FK trỏ về bảng employees (phía owning = Employee)
    //   - inverseJoinColumns: FK trỏ về bảng projects (phía inverse = Project)
    //
    // Không dùng cascade = ALL vì:
    //   - cascade REMOVE sẽ xóa Project khi xóa Employee → SAI!
    //     Một Project có thể còn nhiều Employee khác, không được xóa theo.
    //   - N–N thường không cascade REMOVE để tránh xóa nhầm entity phía bên kia.
    @ManyToMany
    @JoinTable(
        name = "employee_project",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "project_id")
    )
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
     * TODO 5.5 — Helper method đồng bộ 2 chiều khi phân công nhân viên vào project.
     *
     * Tại sao phải add cả 2 phía?
     *   - JPA chỉ tự đồng bộ xuống DB qua owning side (Employee.projects).
     *   - Nhưng trong memory (trong cùng 1 session), nếu chỉ add 1 phía thì
     *     p.getEmployees() sẽ không chứa employee này → dữ liệu không nhất quán
     *     khi đọc lại từ object đang được quản lý (managed entity).
     *   - Dùng helper method để đảm bảo luôn đồng bộ cả 2 chiều, tránh bug khó tìm.
     */
    public void assignToProject(Project p) {
        // Thêm vào phía owning (Employee) → Hibernate sẽ INSERT vào employee_project
        this.projects.add(p);
        // Thêm vào phía inverse (Project) → đồng bộ trong memory
        p.getEmployees().add(this);
    }

    /**
     * TODO 5.9 — Helper method gỡ nhân viên khỏi project (đồng bộ 2 chiều).
     *
     * Tại sao phải remove cả 2 phía?
     *   - Tương tự assignToProject: owning side quyết định DB,
     *     nhưng inverse side phải được cập nhật để nhất quán trong memory.
     *   - Chỉ xóa dòng trong bảng trung gian employee_project,
     *     KHÔNG xóa Employee hay Project gốc.
     */
    public void unassignFromProject(Project p) {
        // Xóa khỏi owning side → Hibernate sẽ DELETE dòng trong employee_project
        this.projects.remove(p);
        // Xóa khỏi inverse side → đồng bộ trong memory
        p.getEmployees().remove(this);
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
