package kolkova_tan;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        StudentDaoImpl studentDao = new StudentDaoImpl();

        // ИЗМЕНЕНИЕ: Весь флоу программы обернут в блок обработки ошибок
        try {
            System.out.println("=== 1. Сохранение студента и его домашек ===");
            Student student = new Student("Елена", "Колкова", "elena@example.com");

            Homework hw1 = new Homework("Изучить JPA Аннотации", LocalDate.now().plusDays(5), 100);
            Homework hw2 = new Homework("Реализовать GenericDao", LocalDate.now().plusDays(7), 95);

            student.addHomework(hw1);
            student.addHomework(hw2);

            studentDao.save(student);

            System.out.println("\n=== 2. Поиск студента по Email ===");
            Student foundByEmail = studentDao.findByEmail("elena@example.com");
            if (foundByEmail != null) {
                System.out.println("Найден: " + foundByEmail);
                // Это сработает без ошибок, так как мы применили JOIN FETCH в DAO
                System.out.println("Домашки студента: " + foundByEmail.getHomeworks());
            } else {
                System.out.println("Студент с таким email не найден.");
            }

            System.out.println("\n=== 3. Обновление данных (Update) ===");
            if (foundByEmail != null) {
                foundByEmail.setLastName("Колкова-Петрова");
                studentDao.update(foundByEmail);
            }

            System.out.println("\n=== 4. Проверка всех записей ===");
            List<Student> all = studentDao.findAll();
            for (Student s : all) {
                System.out.println(s + " | Колич. домашек: " + s.getHomeworks().size());
            }

        } catch (DaoException e) {
            // ИЗМЕНЕНИЕ: Логирование критических ошибок БД согласно замечаниям
            System.err.println("\n[КРИТИЧЕСКАЯ ОШИБКА DAO]: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("[ПЕРВОПРИЧИНА (Cause)]: " + e.getCause().getMessage());
            }
            System.err.println("[СТЕК ТРЕЙС ОШИБКИ]:");
            e.printStackTrace();
        } finally {
            // Ресурсы фабрики закроются в любом случае
            studentDao.close();
        }
    }
}