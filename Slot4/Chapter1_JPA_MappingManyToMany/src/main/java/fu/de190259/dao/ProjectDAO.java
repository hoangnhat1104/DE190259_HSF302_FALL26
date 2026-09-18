package fu.de190259.dao;

import fu.de190259.pojo.Project;
import fu.de190259.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * TODO 5.6 — DAO cho Project — CRUD cơ bản.
 * Mỗi method tự mở/đóng EntityManager riêng.
 * Có try/catch/finally với rollback() khi lỗi và close() trong finally.
 */
public class ProjectDAO {

    /** Lưu mới một Project */
    public void save(Project project) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(project);
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

    /** Lấy tất cả Project */
    public List<Project> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Project p", Project.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Tìm Project theo id */
    public Project findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            em.close();
        }
    }

    /** Cập nhật Project */
    public Project update(Project project) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Project updated = em.merge(project);
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

    /** Xóa Project theo id */
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Project project = em.find(Project.class, id);
            if (project != null) {
                em.remove(project);
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
