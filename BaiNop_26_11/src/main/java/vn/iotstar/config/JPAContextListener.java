package vn.iotstar.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Đóng EntityManagerFactory khi Tomcat dừng / redeploy ứng dụng. */
@WebListener
public class JPAContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAConfig.shutdown();
    }
}
