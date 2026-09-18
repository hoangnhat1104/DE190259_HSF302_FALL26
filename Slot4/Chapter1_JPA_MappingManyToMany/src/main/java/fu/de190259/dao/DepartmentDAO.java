package fu.de190259.dao;

import fu.de190259.pojo.Department;
import fu.de190259.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * TODO 2.5 — DAO cho Department
 * Mỗi method tự mở/đóng EntityManager riêng.
 * Có try/catch/finally với rollback() khi lỗi và close() trong finally.
 */
public class DepartmentDAO {

    /** Lưu mới một Department (cascade ALL tự lo Employee con) */
    public void save(Department department) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(department);
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

    /** Lấy tất cả Department (không load employees — LAZY) */
    public List<Department> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT d FROM Department d", Department.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Tìm Department theo id (không load employees) */
    public Department findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * TODO 2.6 — Tìm Department kèm danh sách Employee bằng JOIN FETCH.
     * Chỉ 1 câu SQL, tránh LazyInitializationException sau khi đóng EM.
     */
    public Department findByIdWithEmployees(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM Department d JOIN FETCH d.employees WHERE d.id = :id",
                            Department.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * TODO 2.9 — Lấy tất cả Department kèm Employee bằng JOIN FETCH.
     * Fix N+1: chỉ 1 câu SQL thay vì 1+N câu.
     */
    public List<Department> findAllWithEmployees() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT d FROM Department d JOIN FETCH d.employees",
                            Department.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Cập nhật Department */
    public Department update(Department department) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Department updated = em.merge(department);
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

    /** Xóa Department theo id (cascade ALL xóa luôn Employee con) */
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Department department = em.find(Department.class, id);
            if (department != null) {
                em.remove(department);
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
