package fu.de190259.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Tiện ích tạo và đóng EntityManagerFactory dùng chung toàn ứng dụng.
 * EMF nặng → chỉ tạo 1 lần (static final).
 * EntityManager nhẹ → tạo mới trong từng method DAO.
 */
public class JPAUtil {

    // "hsf302FU" phải khớp persistence-unit name trong persistence.xml
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("hsf302FU");

    /** Lấy EntityManager mới cho mỗi thao tác */
    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /** Đóng EMF khi kết thúc ứng dụng */
    public static void close() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
