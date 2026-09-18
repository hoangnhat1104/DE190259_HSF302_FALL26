package fu.de190259;

import fu.de190259.dao.EmployeeDAO;
import fu.de190259.dao.ProjectDAO;
import fu.de190259.pojo.Employee;
import fu.de190259.pojo.Gender;
import fu.de190259.pojo.Project;
import fu.de190259.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TODO 5.7 — Demo: tạo 3 Employee, 2 Project, phân công chéo, in danh sách project của từng NV.
 */
public class Main {
    public static void main(String[] args) {

        EmployeeDAO employeeDAO = new EmployeeDAO();
        ProjectDAO projectDAO = new ProjectDAO();

        // ── Bước 1: Tạo 3 Employee ────────────────────────────────────────────
        // Điền đủ: salary, hireDate, gender, active (mặc định true)
        Employee e1 = new Employee(
                "nguyen.van.a@company.com", "Nguyen Van A",
                Gender.MALE, new BigDecimal("20000000"), LocalDate.of(2021, 3, 1));

        Employee e2 = new Employee(
                "tran.thi.b@company.com", "Tran Thi B",
                Gender.FEMALE, new BigDecimal("25000000"), LocalDate.of(2020, 7, 15));

        Employee e3 = new Employee(
                "le.van.c@company.com", "Le Van C",
                Gender.OTHER, new BigDecimal("18000000"), LocalDate.of(2023, 1, 10));

        employeeDAO.save(e1);
        employeeDAO.save(e2);
        employeeDAO.save(e3);
        System.out.println("Da luu 3 Employee:");
        System.out.println("  e1.id=" + e1.getId() + " - " + e1.getFullName());
        System.out.println("  e2.id=" + e2.getId() + " - " + e2.getFullName());
        System.out.println("  e3.id=" + e3.getId() + " - " + e3.getFullName());

        // ── Bước 2: Tạo 2 Project ────────────────────────────────────────────
        // endDate = null nếu dự án chưa kết thúc
        Project pA = new Project(
                "PRJ-ALPHA", "Du An Alpha",
                new BigDecimal("500000000"), LocalDate.of(2024, 1, 1), null);

        Project pB = new Project(
                "PRJ-BETA", "Du An Beta",
                new BigDecimal("300000000"), LocalDate.of(2024, 6, 1), LocalDate.of(2025, 6, 30));

        projectDAO.save(pA);
        projectDAO.save(pB);
        System.out.println("\nDa luu 2 Project:");
        System.out.println("  pA.id=" + pA.getId() + " - " + pA.getProjectName());
        System.out.println("  pB.id=" + pB.getId() + " - " + pB.getProjectName());

        // ── Bước 3: Phân công chéo ────────────────────────────────────────────
        // NV1 (Nguyen Van A)  → Project A + Project B
        // NV2 (Tran Thi B)    → Project B
        // NV3 (Le Van C)      → Project A
        System.out.println("\nPhan cong:");
        employeeDAO.assignEmployeeToProject(e1.getId(), pA.getId());
        System.out.println("  " + e1.getFullName() + " -> " + pA.getProjectName());

        employeeDAO.assignEmployeeToProject(e1.getId(), pB.getId());
        System.out.println("  " + e1.getFullName() + " -> " + pB.getProjectName());

        employeeDAO.assignEmployeeToProject(e2.getId(), pB.getId());
        System.out.println("  " + e2.getFullName() + " -> " + pB.getProjectName());

        employeeDAO.assignEmployeeToProject(e3.getId(), pA.getId());
        System.out.println("  " + e3.getFullName() + " -> " + pA.getProjectName());

        // ── Bước 4: In danh sách project của từng nhân viên ───────────────────
        // Dùng JOIN FETCH để tránh LazyInitializationException sau khi EM đóng
        System.out.println("\n=== Danh sach Project cua tung nhan vien ===");
        for (Employee emp : employeeDAO.findAllWithProjects()) {
            System.out.println(emp.getFullName() + " tham gia " + emp.getProjects().size() + " project:");
            for (Project p : emp.getProjects()) {
                System.out.println("    - [" + p.getProjectCode() + "] " + p.getProjectName());
            }
        }

        JPAUtil.close();
    }
}
