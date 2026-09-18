package fu.de190259.dao;

import fu.de190259.pojo.Employee;
import fu.de190259.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * TODO 2.5 — DAO cho Employee
 * Mỗi method tự mở/đóng EntityManager riêng.
 * Có try/catch/finally với rollback() khi lỗi và close() trong finally.
 */
public class EmployeeDAO {

    /** Lưu mới một Employee (Employee phải đã được set Department) */
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
}
