package fu.de190259;

import fu.de190259.dao.DepartmentDAO;
import fu.de190259.pojo.Department;
import fu.de190259.pojo.Employee;
import fu.de190259.pojo.Gender;
import fu.de190259.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        DepartmentDAO departmentDAO = new DepartmentDAO();

        // ── TODO 2.7 — Tạo 1 Department + 3 Employee ─────────────────────────
        Department it = new Department("Marketing", "Ha Noi");

        Employee e1 = new Employee("aa.nguyen@company.com", "Nguyen Van A", Gender.MALE,
                new BigDecimal("15000000"), LocalDate.of(2022, 1, 10));
        Employee e2 = new Employee("bb.tran@company.com", "Tran Thi B", Gender.FEMALE,
                new BigDecimal("18000000"), LocalDate.of(2021, 6, 1));
        Employee e3 = new Employee("cc.le@company.com", "Le Van C", Gender.OTHER,
                new BigDecimal("12000000"), LocalDate.of(2023, 3, 15));

        // Dùng helper method addEmployee() — đồng bộ 2 chiều (TODO 2.4)
        it.addEmployee(e1);
        it.addEmployee(e2);
        it.addEmployee(e3);

        // Chỉ persist(department) — cascade = ALL tự lo phần Employee
        departmentDAO.save(it);
        System.out.println("Da luu Department, id = " + it.getId());

        // ── TODO 2.6 — Tìm lại kèm employees bằng JOIN FETCH ─────────────────
        // Không bị LazyInitializationException vì employees đã load trong cùng 1 query
        Department found = departmentDAO.findByIdWithEmployees(it.getId());
        System.out.println("Phong ban: " + found.getName());
        for (Employee e : found.getEmployees()) {
            System.out.println("  - " + e);
        }

        // ── TODO 2.8 — Tái hiện N+1 Query Problem ────────────────────────────
        // findAll() = 1 câu SELECT departments
        // Mỗi .getEmployees() kích hoạt thêm 1 câu SELECT employees riêng
        // Tổng: 1 + N câu SQL (N = số department)
        System.out.println("\n=== TODO 2.8: N+1 Query Problem ===");
        System.out.println("(Kiem tra console: thay 1 + N cau SQL)");
        for (Department d : departmentDAO.findAll()) {
            System.out.println(d.getName() + " co " + d.getEmployees().size() + " nhan vien");
        }

        // ── TODO 2.9 — Fix N+1 bằng JOIN FETCH ───────────────────────────────
        // findAllWithEmployees() = chỉ 1 câu SQL có JOIN
        // Truoc fix: 1 + N cau | Sau fix: 1 cau duy nhat
        System.out.println("\n=== TODO 2.9: Fix N+1 bang JOIN FETCH ===");
        System.out.println("(Kiem tra console: chi con 1 cau SQL co JOIN)");
        for (Department d : departmentDAO.findAllWithEmployees()) {
            System.out.println(d.getName() + " co " + d.getEmployees().size() + " nhan vien");
        }

        JPAUtil.close();
    }
}
