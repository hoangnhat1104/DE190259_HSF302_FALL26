package fu.de190259;

import fu.de190259.dao.EmployeeDAO;
import fu.de190259.entity.Employee;
import fu.de190259.entity.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        EmployeeDAO dao = new EmployeeDAO();

        System.out.println("========================================");
        System.out.println("         DEMO CRUD - JPA/Hibernate      ");
        System.out.println("========================================");

        // ============================================================
        // BƯỚC 1 — CREATE
        // Trạng thái: entity đang NEW/TRANSIENT (chưa có id, chưa liên kết EM)
        // ============================================================
        System.out.println("\n--- BƯỚC 1: CREATE ---");

        Employee emp = new Employee(
                "Nguyen Van A",
                "vana@example.com",
                new BigDecimal("15000000"),
                Gender.MALE,
                LocalDate.of(2020, 6, 1),
                true
        );
        // Trước save(): entity đang NEW/TRANSIENT
        System.out.println("Trước save() → trạng thái: NEW/TRANSIENT, id = " + emp.getId());

        dao.save(emp);

        // Sau save() return (EntityManager đã đóng): entity đang DETACHED
        System.out.println("Sau save()   → trạng thái: DETACHED, id = " + emp.getId());

        // ============================================================
        // BƯỚC 2 — READ (findById)
        // ============================================================
        System.out.println("\n--- BƯỚC 2: READ findById ---");

        Employee found = dao.findById(emp.getId());
        // found được trả về sau khi EM đóng → DETACHED
        System.out.println("findById(" + emp.getId() + ") → " + found);

        // ============================================================
        // BƯỚC 3 — UPDATE
        // ============================================================
        System.out.println("\n--- BƯỚC 3: UPDATE ---");

        // found đang DETACHED — sửa salary rồi gọi update()
        found.setSalary(new BigDecimal("20000000"));
        System.out.println("Trước update() → trạng thái: DETACHED, salary mới = " + found.getSalary());

        Employee updated = dao.update(found);
        // object trả về từ merge() là MANAGED (trong transaction đó)
        // found truyền vào vẫn DETACHED
        System.out.println("Sau update()   → managed.salary = " + updated.getSalary());

        // ============================================================
        // BƯỚC 4 — READ lại để kiểm tra UPDATE
        // ============================================================
        System.out.println("\n--- BƯỚC 4: READ lại sau UPDATE ---");

        Employee afterUpdate = dao.findById(emp.getId());
        System.out.println("findById sau update → salary = " + afterUpdate.getSalary());

        // ============================================================
        // BƯỚC 5 — DELETE
        // ============================================================
        System.out.println("\n--- BƯỚC 5: DELETE ---");

        System.out.println("Xóa employee id = " + emp.getId());
        dao.delete(emp.getId());
        // Sau delete(): entity chuyển sang REMOVED trong transaction,
        // biến mất khỏi DB sau commit()
        System.out.println("Đã xóa → trạng thái: REMOVED/biến mất khỏi DB");

        // ============================================================
        // BƯỚC 6 — READ lại để kiểm tra DELETE
        // ============================================================
        System.out.println("\n--- BƯỚC 6: READ lại sau DELETE ---");

        Employee afterDelete = dao.findById(emp.getId());
        if (afterDelete == null) {
            System.out.println("findById(" + emp.getId() + ") → null (đã xóa thành công)");
        } else {
            System.out.println("findById → " + afterDelete + " (XÓA THẤT BẠI!)");
        }

        System.out.println("\n========================================");
        System.out.println("     DEMO CRUD HOÀN TẤT - KHÔNG LỖI    ");
        System.out.println("========================================");

        // ============================================================
        // BƯỚC 7 — TODO 0.9: Kiểm chứng ràng buộc unique trên email
        // ============================================================
        System.out.println("\n--- BƯỚC 7: KIỂM TRA UNIQUE EMAIL ---");

        // Tạo employee thứ nhất với email "unique@example.com"
        Employee emp1 = new Employee(
                "Tran Thi B",
                "unique@example.com",
                new BigDecimal("12000000"),
                Gender.FEMALE,
                LocalDate.of(2021, 1, 15),
                true
        );
        dao.save(emp1);
        System.out.println("Save emp1 thành công → id = " + emp1.getId());

        // Cố ý tạo employee thứ hai với CÙNG email → phải ném exception
        Employee emp2 = new Employee(
                "Le Van C",
                "unique@example.com",   // email trùng với emp1
                new BigDecimal("13000000"),
                Gender.MALE,
                LocalDate.of(2022, 3, 10),
                true
        );

        try {
            dao.save(emp2);
            System.out.println("CẢNH BÁO: Save emp2 thành công — unique constraint KHÔNG hoạt động!");
        } catch (Exception e) {
            // Kỳ vọng: ConstraintViolationException / PersistenceException
            System.out.println("ĐÚNG KỲ VỌNG: Save emp2 thất bại vì email trùng!");
            System.out.println("Loại exception: " + e.getClass().getSimpleName());
            System.out.println("Thông báo: " + e.getMessage());
        }

        // Dọn dẹp — xóa emp1 sau khi test
        dao.delete(emp1.getId());
        System.out.println("Đã xóa emp1 sau khi test unique.");

        System.out.println("\n========================================");
        System.out.println("     DEMO HOÀN TẤT - TẤT CẢ PASS!      ");
        System.out.println("========================================");
    }
}
