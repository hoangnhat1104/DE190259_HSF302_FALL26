package fu.de190259.pojo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * TODO 5.1 — Entity Project
 * Đại diện cho bảng "projects" trong DB.
 *
 * Quan hệ N–N với Employee:
 *   - Project là INVERSE SIDE (mappedBy = "projects")
 *   - Không giữ FK, không dùng @JoinTable ở đây
 *
 * Dùng Set<Employee> thay vì List<Employee>:
 *   - Tránh trùng lặp (mỗi nhân viên chỉ xuất hiện 1 lần trong 1 project)
 *   - Hibernate dùng equals()/hashCode() để kiểm tra trùng trong Set
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // projectCode là business key — unique, not null
    @Column(name = "project_code", unique = true, nullable = false)
    private String projectCode;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "budget")
    private BigDecimal budget;

    // LocalDate — JPA 2.2+ map thẳng, không cần @Temporal
    @Column(name = "start_date")
    private LocalDate startDate;

    // Nullable: dự án chưa kết thúc thì endDate = null
    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    // TODO 5.3 — Inverse side của quan hệ N–N
    // mappedBy = "projects" trỏ về tên field Set<Project> ở Employee (owning side)
    //
    // Tại sao KHÔNG dùng cascade = ALL ở đây?
    // → Nếu cascade REMOVE: xóa 1 Project sẽ kéo theo xóa tất cả Employee liên quan — SAI!
    //   Employee có thể đang làm việc ở nhiều project khác, không thể bị xóa theo project.
    // → N–N thường chỉ cascade PERSIST/MERGE nếu cần, hoặc không cascade gì cả.
    @ManyToMany(mappedBy = "projects")
    private Set<Employee> employees = new HashSet<>();

    // Constructor không tham số — bắt buộc với JPA
    public Project() {
    }

    public Project(String projectCode, String projectName, BigDecimal budget,
                   LocalDate startDate, LocalDate endDate) {
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    /**
     * TODO 5.4 — equals() dựa trên projectCode (business key), KHÔNG dùng id.
     *
     * Lý do không dùng id:
     *   - Trước khi persist(), id = null → 2 object chưa lưu sẽ bị coi là bằng nhau (đều null).
     *   - Sau persist(), id được gán → equals() thay đổi hành vi → vi phạm hợp đồng equals().
     *   - Dùng projectCode (luôn có giá trị, unique) đảm bảo tính nhất quán trong cả vòng đời entity.
     *   - Đặc biệt quan trọng khi entity được đặt trong Set hoặc Map.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equals(projectCode, project.projectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectCode);
    }

    @Override
    public String toString() {
        return "Project{id=" + id
                + ", projectCode='" + projectCode + "'"
                + ", projectName='" + projectName + "'"
                + ", budget=" + budget
                + ", startDate=" + startDate
                + ", endDate=" + (endDate != null ? endDate : "ongoing") + "}";
    }
}
