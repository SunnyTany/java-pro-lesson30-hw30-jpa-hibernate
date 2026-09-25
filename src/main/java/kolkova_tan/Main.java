package kolkova_tan;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        StudentDaoImpl studentDao = new StudentDaoImpl();

        System.out.println("=== 1. Сохранение студента и его домашек ===");
        Student student = new Student("Елена", "Колкова", "elena@example.com");

        Homework hw1 = new Homework("Изучить JPA Аннотации", LocalDate.now().plusDays(5), 100);
        Homework hw2 = new Homework("Реализовать GenericDao", LocalDate.now().plusDays(7), 95);

        // Using our related methods
        student.addHomework(hw1);
        student.addHomework(hw2);

        // Saving the student (thanks to CascadeType.ALL, homework will be saved automatically)
        studentDao.save(student);

        System.out.println("\n=== 2. Поиск студента по Email ===");
        Student foundByEmail = studentDao.findByEmail("elena@example.com");
        System.out.println("Найден: " + foundByEmail);
        System.out.println("Домашки студента: " + foundByEmail.getHomeworks());

        System.out.println("\n=== 3. Обновление данных (Update) ===");
        foundByEmail.setLastName("Колкова-Петрова");
        studentDao.update(foundByEmail);

        System.out.println("\n=== 4. Проверка всех записей ===");
        List<Student> all = studentDao.findAll();
        for (Student s : all) {
            System.out.println(s + " | Колич. домашек: " + s.getHomeworks().size());
        }

        // Cleaning the resource factory
        studentDao.close();
    }
}