package fu.de190259.pojo;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO 2.1 — Entity Department
 * Đại diện cho bảng "departments" trong DB.
 *
 * TODO 2.3 — Inverse side (@OneToMany)
 * Department là phía "One", không giữ FK.
 * mappedBy = "department" trỏ về tên field trong Employee (owning side).
 * cascade = ALL: các thao tác persist/merge/remove lan sang Employee.
 * orphanRemoval = true: xóa Employee khỏi list → xóa hẳn khỏi DB.
 */
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "location")
    private String location;

    // TODO 2.3 — Inverse side
    // mappedBy = "department" phải khớp chính xác tên field ở Employee
    // Khởi tạo sẵn ArrayList để tránh NullPointerException khi gọi .add()
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    // Constructor không tham số — bắt buộc với JPA
    public Department() {
    }

    public Department(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    // TODO 2.4 — Helper methods đồng bộ 2 chiều

    /**
     * Thêm Employee vào phòng ban.
     * Đồng thời set department cho Employee để 2 phía luôn nhất quán trong memory.
     */
    public void addEmployee(Employee e) {
        this.employees.add(e);
        e.setDepartment(this);
    }

    /**
     * Xóa Employee khỏi phòng ban.
     * Đồng thời clear department của Employee.
     * Nhờ orphanRemoval = true, Employee sẽ bị xóa khỏi DB khi flush.
     */
    public void removeEmployee(Employee e) {
        this.employees.remove(e);
        e.setDepartment(null);
    }

    @Override
    public String toString() {
        return "Department{id=" + id + ", name='" + name + "', location='" + location + "'}";
    }
}
