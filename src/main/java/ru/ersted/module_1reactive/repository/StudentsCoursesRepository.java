package ru.ersted.module_1reactive.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.entity.Student;
import ru.ersted.module_1reactive.entity.StudentsCourses;

public interface StudentsCoursesRepository extends ReactiveCrudRepository<StudentsCourses, Void> {

    @Query("""
            SELECT c.* FROM course c
            JOIN students_courses sc ON c.id = sc.course_id
            WHERE sc.student_id = :studentId
            """)
    Flux<Course> findCoursesByStudentId(Long studentId);

    @Query("""
            SELECT s.* FROM student s 
            JOIN students_courses sc ON s.id = sc.student_id
            WHERE sc.course_id = :courseId
            """)
    Flux<Student> findStudentsByCourseId(Long courseId);

}
