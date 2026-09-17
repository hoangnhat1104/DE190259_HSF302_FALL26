package fu.de190259.dao;

import fu.de190259.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

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
}
