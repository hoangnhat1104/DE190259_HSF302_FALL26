package fu.de190259;

import fu.de190259.dao.EmployeeDAO;
import fu.de190259.entity.Employee;
import fu.de190259.entity.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TODO 0.10 — Entity Lifecycle trong JPA:
 *
 *  NEW/TRANSIENT : object vừa new, chưa liên kết với EntityManager, chưa có id
 *  MANAGED       : đang được EntityManager theo dõi trong transaction (dirty checking)
 *  DETACHED      : từng Managed nhưng EntityManager đã đóng / clear
 *  REMOVED       : đã gọi remove(), sẽ bị xóa khỏi DB sau commit
 */
public class Main {
    public static void main(String[] args) {

        EmployeeDAO dao = new EmployeeDAO();

        System.out.println("========================================");
        System.out.println("         DEMO CRUD - JPA/Hibernate      ");
        System.out.println("========================================");

        // ============================================================
        // BƯỚC 1 — CREATE
        // ============================================================
        System.out.println("\n--- BƯỚC 1: CREATE ---");

        // TODO 0.10: Trước save() → entity đang NEW/TRANSIENT
        // (vừa new, chưa liên kết EM, id = null)
        Employee emp = new Employee(
                "Nguyen Van A",
                "vana@example.com",
                new BigDecimal("15000000"),
                Gender.MALE,
                LocalDate.of(2020, 6, 1),
                true
        );
        System.out.println("Trước save() → NEW/TRANSIENT, id = " + emp.getId());

        dao.save(emp);

        // TODO 0.10: Ngay sau persist() trong transaction → MANAGED
        //            Sau commit() + em.close() → DETACHED
        //            Vì save() đã đóng EM, nên khi return về đây entity là DETACHED
        System.out.println("Sau save()   → DETACHED, id = " + emp.getId());

        // ============================================================
        // BƯỚC 2 — READ (findById)
        // ============================================================
        System.out.println("\n--- BƯỚC 2: READ findById ---");

        // TODO 0.10: findById trả về entity sau khi EM đóng → DETACHED
        Employee found = dao.findById(emp.getId());
        System.out.println("findById(" + emp.getId() + ") → DETACHED, " + found);

        // ============================================================
        // BƯỚC 3 — UPDATE
        // ============================================================
        System.out.println("\n--- BƯỚC 3: UPDATE ---");

        // TODO 0.10: found đang DETACHED — sửa field rồi gọi update()
        found.setSalary(new BigDecimal("20000000"));
        System.out.println("Trước update() → DETACHED, salary mới = " + found.getSalary());

        // TODO 0.10: Sau update() (gọi merge()):
        //   - object trả về từ merge() là MANAGED (trong transaction đó)
        //   - object 'found' truyền vào vẫn DETACHED
        Employee updated = dao.update(found);
        System.out.println("Sau update()   → updated là MANAGED (đã commit), salary = " + updated.getSalary());

        // ============================================================
        // BƯỚC 4 — READ lại để kiểm tra UPDATE
        // ============================================================
        System.out.println("\n--- BƯỚC 4: READ lại sau UPDATE ---");

        Employee afterUpdate = dao.findById(emp.getId());
        System.out.println("findById sau update → DETACHED, salary = " + afterUpdate.getSalary());

        // ============================================================
        // BƯỚC 5 — DELETE
        // ============================================================
        System.out.println("\n--- BƯỚC 5: DELETE ---");

        System.out.println("Xóa employee id = " + emp.getId());
        dao.delete(emp.getId());

        // TODO 0.10: Sau delete():
        //   - Trong transaction: entity chuyển sang REMOVED
        //   - Sau commit(): entity biến mất khỏi DB hoàn toàn
        System.out.println("Sau delete() + commit() → REMOVED, biến mất khỏi DB");

        // ============================================================
        // BƯỚC 6 — READ lại để kiểm tra DELETE
        // ============================================================
        System.out.println("\n--- BƯỚC 6: READ lại sau DELETE ---");

        Employee afterDelete = dao.findById(emp.getId());
        if (afterDelete == null) {
            System.out.println("findById(" + emp.getId() + ") → null (xóa thành công ✓)");
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

        // TODO 0.10: emp1 trước save() → NEW/TRANSIENT
        Employee emp1 = new Employee(
                "Tran Thi B",
                "unique@example.com",
                new BigDecimal("12000000"),
                Gender.FEMALE,
                LocalDate.of(2021, 1, 15),
                true
        );
        dao.save(emp1);
        // TODO 0.10: sau save() → DETACHED
        System.out.println("Save emp1 thành công → DETACHED, id = " + emp1.getId());

        // Cố ý tạo employee thứ hai với CÙNG email → phải ném exception
        // TODO 0.10: emp2 → NEW/TRANSIENT, sẽ không bao giờ thành MANAGED vì lỗi unique
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
