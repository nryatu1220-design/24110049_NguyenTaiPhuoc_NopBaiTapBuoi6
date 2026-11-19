package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Giữ MỘT EntityManagerFactory dùng chung cho cả ứng dụng
 * (tạo factory rất tốn kém nên không tạo lại mỗi lần gọi DAO).
 */
public class JPAConfig {

    private static final String PERSISTENCE_UNIT = "jpa-shopping";

    private static EntityManagerFactory factory;

    private JPAConfig() {
    }

    private static synchronized EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return factory;
    }

    /** Mỗi lần gọi trả về một EntityManager mới; nơi gọi phải close(). */
    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    /** Gọi khi ứng dụng dừng (xem JPAContextListener). */
    public static synchronized void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
