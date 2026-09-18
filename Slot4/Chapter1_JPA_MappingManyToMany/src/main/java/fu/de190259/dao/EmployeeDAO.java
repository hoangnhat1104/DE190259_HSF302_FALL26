package fu.de190259.dao;

import fu.de190259.pojo.Employee;
import fu.de190259.pojo.Project;
import fu.de190259.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * DAO cho Employee — CRUD cơ bản + phân công project.
 * Mỗi method tự mở/đóng EntityManager riêng.
 * Có try/catch/finally với rollback() khi lỗi và close() trong finally.
 */
public class EmployeeDAO {

    /** Lưu mới một Employee */
    public void save(Employee employee) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(employee);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Lấy tất cả Employee */
    public List<Employee> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e", Employee.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Tìm Employee theo id */
    public Employee findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Employee.class, id);
        } finally {
            em.close();
        }
    }

    /** Cập nhật Employee — dùng merge(), gán lại kết quả trả về */
    public Employee update(Employee employee) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Employee updated = em.merge(employee);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Xóa Employee theo id */
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Employee employee = em.find(Employee.class, id);
            if (employee != null) {
                em.remove(employee);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * TODO 5.6 — Phân công Employee vào Project trong 1 transaction.
     *
     * Tại sao phải find cả 2 entity trong cùng 1 EntityManager (cùng 1 transaction)?
     *   - em.find() trả về managed entity — Hibernate theo dõi mọi thay đổi.
     *   - Khi gọi assignToProject(), cả Employee.projects và Project.employees
     *     đều được cập nhật trong memory.
     *   - Khi commit(), Hibernate phát hiện owning side (Employee.projects) thay đổi
     *     → tự động INSERT dòng mới vào bảng trung gian employee_project.
     *   - Không cần gọi em.persist() hay em.merge() thêm vì entity đã là managed.
     *
     * @param employeeId id của Employee cần phân công
     * @param projectId  id của Project cần phân công vào
     */
    public void assignEmployeeToProject(Long employeeId, Long projectId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // find() trong cùng 1 EM → cả 2 đều là managed entity
            Employee employee = em.find(Employee.class, employeeId);
            Project project = em.find(Project.class, projectId);

            if (employee == null) {
                throw new IllegalArgumentException("Khong tim thay Employee id=" + employeeId);
            }
            if (project == null) {
                throw new IllegalArgumentException("Khong tim thay Project id=" + projectId);
            }

            // Gọi helper method — đồng bộ 2 chiều trong memory
            // Hibernate tự detect thay đổi ở owning side → INSERT vào employee_project
            employee.assignToProject(project);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * TODO 5.9 — Gỡ Employee khỏi Project trong 1 transaction.
     *
     * Chỉ xóa dòng trong bảng trung gian employee_project.
     * Không ảnh hưởng đến bảng employees hay projects.
     *
     * @param employeeId id của Employee cần gỡ
     * @param projectId  id của Project cần gỡ khỏi
     */
    public void unassignEmployeeFromProject(Long employeeId, Long projectId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Employee employee = em.find(Employee.class, employeeId);
            Project project = em.find(Project.class, projectId);

            if (employee == null) {
                throw new IllegalArgumentException("Khong tim thay Employee id=" + employeeId);
            }
            if (project == null) {
                throw new IllegalArgumentException("Khong tim thay Project id=" + projectId);
            }

            // Gọi helper method — đồng bộ 2 chiều trong memory
            // Hibernate tự detect thay đổi ở owning side → DELETE khỏi employee_project
            employee.unassignFromProject(project);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * TODO 5.11 — Deactivate Employee (set active = false).
     *
     * Tại sao KHÔNG tự động gỡ khỏi tất cả project?
     *   - Dữ liệu tham gia project là lịch sử quan trọng: ai đã làm dự án nào, bao giờ.
     *   - Xóa quan hệ trong employee_project sẽ mất lịch sử → không tra cứu được sau này.
     *   - Không dùng cascade REMOVE vì Employee và Project có vòng đời độc lập.
     *   - Nếu muốn gỡ khỏi project, phải gọi unassignEmployeeFromProject() riêng — có chủ ý.
     *   - Query tìm nhân viên active (e.active = true) đã lọc sẵn → báo cáo vẫn đúng.
     *
     * @param employeeId id của Employee cần deactivate
     */
    public void deactivateEmployee(Long employeeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Employee employee = em.find(Employee.class, employeeId);
            if (employee == null) {
                throw new IllegalArgumentException("Khong tim thay Employee id=" + employeeId);
            }

            // Chỉ set active = false, KHÔNG xóa quan hệ trong employee_project
            // Dữ liệu tham gia project vẫn còn để tra cứu lịch sử
            employee.setActive(false);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
