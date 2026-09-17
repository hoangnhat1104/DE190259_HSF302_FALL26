package fu.de190259.dao;

import fu.de190259.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class EmployeeDAO {

    // EntityManagerFactory — khởi tạo 1 lần, dùng chung
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("chapter1-jpa-ex1");

    // ================================================================
    // TODO 0.3 — CREATE: persist 1 Employee mới trong 1 transaction
    // ================================================================
    public void save(Employee e) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();         // mở transaction

            em.persist(e);                       // INSERT vào DB

            em.getTransaction().commit();        // commit → DB sinh ID
            // Sau commit: e.getId() != null — entity đã Managed → insert xong
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();  // rollback nếu lỗi
            }
            throw ex;
        } finally {
            em.close();                          // luôn đóng EntityManager
        }
    }

    // ================================================================
    // TODO 0.4 — READ: findById
    // ================================================================
    public Employee findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            // em.find trả về null nếu không tìm thấy, không ném exception
            return em.find(Employee.class, id);
        } finally {
            em.close();
        }
    }

    // ================================================================
    // TODO 0.4 — READ: findAll
    // ================================================================
    public List<Employee> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            // JPQL — dùng tên class Employee, không phải tên bảng employees
            return em.createQuery("SELECT e FROM Employee e", Employee.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    // ================================================================
    // TODO 0.5 — READ có điều kiện (JPQL)
    // ================================================================

    // Tìm nhân viên theo email — dùng để check trùng trước khi tạo mới
    // Trả về null nếu không tìm thấy (không ném exception)
    public Employee findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> result = em.createQuery(
                    "SELECT e FROM Employee e WHERE e.email = :email",
                    Employee.class)
                    .setParameter("email", email)   // không nối chuỗi trực tiếp
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    // Tìm danh sách nhân viên có salary lớn hơn mức cho trước
    public List<Employee> findBySalaryGreaterThan(java.math.BigDecimal minSalary) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e WHERE e.salary > :minSalary",
                    Employee.class)
                    .setParameter("minSalary", minSalary)  // không nối chuỗi trực tiếp
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Tìm danh sách nhân viên đang active
    public List<Employee> findAllActive() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Employee e WHERE e.active = true",
                    Employee.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ================================================================
    // TODO 0.6 — UPDATE: cập nhật thông tin Employee đã tồn tại
    // ================================================================
    public Employee update(Employee e) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // merge() vì entity có thể đang ở trạng thái Detached
            // object trả về từ merge() là Managed, object cũ vẫn Detached
            Employee managed = em.merge(e);

            em.getTransaction().commit();
            return managed;  // trả về entity Managed sau khi merge
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    // ================================================================
    // TODO 0.7 — DELETE: xóa Employee theo id
    // ================================================================
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // find() trước để đảm bảo entity đang Managed trước khi remove
            Employee e = em.find(Employee.class, id);
            if (e != null) {
                em.remove(e);   // entity chuyển sang Removed trong transaction
                // sau commit → biến mất khỏi DB
            }

            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
