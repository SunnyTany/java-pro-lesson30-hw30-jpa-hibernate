package kolkova_tan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class StudentDaoImpl implements GenericDao<Student, Long> {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hillel-persistence-unit");

    @Override
    public void save(Student entity) {
        // Использование try-with-resources автоматически закроет EntityManager
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(entity);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new DaoException("Не удалось сохранить студента с email: " + entity.getEmail(), e);
            }
        }
    }

    @Override
    public Student findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Student student = em.find(Student.class, id);
            if (student == null) {
                throw new DaoException("Студент с ID " + id + " не найден в базе данных", null);
            }
            return student;
        } catch (DaoException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoException("Ошибка при поиске студента по ID: " + id, e);
        }
    }

    @Override
    public Student findByEmail(String email) {
        try (EntityManager em = emf.createEntityManager()) {
            // Использование JOIN FETCH решает проблему LazyInitializationException,
            // так как подгружает коллекции за один запрос
            List<Student> students = em.createQuery(
                            "SELECT s FROM Student s LEFT JOIN FETCH s.homeworks WHERE s.email = :email", Student.class)
                    .setParameter("email", email)
                    .getResultList(); // Безопасное получение списка вместо getSingleResult()

            return students.isEmpty() ? null : students.get(0);
        } catch (Exception e) {
            throw new DaoException("Ошибка при поиске студента по email: " + email, e);
        }
    }

    @Override
    public List<Student> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            // Здесь подгружаем с JOIN FETCH, чтобы в цикле Main'а не было N+1 запросов и Lazy сессии
            return em.createQuery("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.homeworks", Student.class)
                    .getResultList();
        } catch (Exception e) {
            throw new DaoException("Ошибка при получении списка всех студентов", e);
        }
    }

    @Override
    public Student update(Student entity) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Student updatedStudent = em.merge(entity);
                tx.commit();
                return updatedStudent;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new DaoException("Не удалось обновить данные студента: " + entity.getEmail(), e);
            }
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Student student = em.find(Student.class, id);
                if (student != null) {
                    em.remove(student);
                    tx.commit();
                    return true;
                }
                tx.commit();
                return false;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new DaoException("Ошибка при удалении студента с ID: " + id, e);
            }
        }
    }

    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}