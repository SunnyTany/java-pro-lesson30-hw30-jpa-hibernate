package kolkova_tan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class StudentDaoImpl implements GenericDao<Student, Long> {

    // The factory name must strictly match the persistence-unit name in persistence.xml
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hillel-persistence-unit");

    @Override
    public void save(Student entity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    public Student findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Student.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Student findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Student s WHERE s.email = :email", Student.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Если студент с таким email не найден
        } finally {
            em.close();
        }
    }

    @Override
    public List<Student> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Student update(Student entity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Student updatedStudent = null;
        try {
            tx.begin();
            updatedStudent = em.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return updatedStudent;
    }

    @Override
    public boolean deleteById(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean success = false;
        try {
            tx.begin();
            Student student = em.find(Student.class, id);
            if (student != null) {
                em.remove(student);
                success = true;
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return success;
    }

    // Method to close the factory when the application terminates
    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}