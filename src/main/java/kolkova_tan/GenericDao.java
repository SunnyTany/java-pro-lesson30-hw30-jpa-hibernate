package kolkova_tan;

import java.util.List;

public interface GenericDao<T, ID> {
    void save(T entity);
    T findById(ID id);
    T findByEmail(String email);
    List<T> findAll();
    Student update(T entity); // Возвращает Student согласно сигнатуре задания
    boolean deleteById(ID id);
}