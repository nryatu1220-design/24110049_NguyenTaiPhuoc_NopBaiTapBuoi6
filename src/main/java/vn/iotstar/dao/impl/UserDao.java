package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.IUserDao;
import vn.iotstar.entity.User;

public class UserDao implements IUserDao {

    @Override
    public void insert(User user) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.persist(user);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void update(User user) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.merge(user);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void delete(int userId) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            User user = enma.find(User.class, userId);
            if (user != null) {
                enma.remove(user);
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public User findById(int userId) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            return enma.find(User.class, userId);
        } finally {
            enma.close();
        }
    }

    @Override
    public User findByUsername(String username) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            List<User> list = enma.createNamedQuery("User.findByUsername", User.class)
                    .setParameter("username", username).setMaxResults(1).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            enma.close();
        }
    }

    @Override
    public User findByEmail(String email) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            List<User> list = enma.createNamedQuery("User.findByEmail", User.class)
                    .setParameter("email", email).setMaxResults(1).getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            enma.close();
        }
    }
}
