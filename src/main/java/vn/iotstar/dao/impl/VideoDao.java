package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.IVideoDao;
import vn.iotstar.entity.Video;

public class VideoDao implements IVideoDao {

    @Override
    public void insert(Video video) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.persist(video);
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
    public void update(Video video) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.merge(video);
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
    public void delete(String videoId) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            Video video = enma.find(Video.class, videoId);
            if (video != null) {
                enma.remove(video);
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
    public Video findById(String videoId) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            return enma.find(Video.class, videoId);
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findLatestActive(int limit) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            return enma.createQuery(
                    "SELECT v FROM Video v WHERE v.active = true ORDER BY v.createdDate DESC, v.videoId DESC",
                    Video.class).setMaxResults(limit).getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findByUser(int userId, int page, int pagesize) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            return enma.createQuery(
                    "SELECT v FROM Video v WHERE v.user.userId = :uid ORDER BY v.createdDate DESC, v.videoId DESC",
                    Video.class)
                    .setParameter("uid", userId)
                    .setFirstResult(page * pagesize)
                    .setMaxResults(pagesize)
                    .getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public long countByUser(int userId) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            return enma.createQuery("SELECT COUNT(v) FROM Video v WHERE v.user.userId = :uid", Long.class)
                    .setParameter("uid", userId).getSingleResult();
        } finally {
            enma.close();
        }
    }

    @Override
    public void increaseViews(String videoId) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            // views có thể đang NULL ở dòng cũ -> COALESCE
            enma.createQuery("UPDATE Video v SET v.views = COALESCE(v.views, 0) + 1 WHERE v.videoId = :id")
                    .setParameter("id", videoId).executeUpdate();
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
}
