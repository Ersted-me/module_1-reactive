package ru.ersted.module_1reactive.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import ru.ersted.module_1reactive.config.DatabaseConfig;
import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.entity.Student;
import ru.ersted.module_1reactive.entity.StudentsCourses;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@Import(DatabaseConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class StudentsCoursesRepositoryTest {


    @Autowired
    StudentsCoursesRepository SCrepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Test
    @DisplayName("Test find all courses by student id")
    @DirtiesContext
    void givenStudentId_whenFindAllCoursesByStudentId_thenReturnCourses() {
        Mono<Tuple2<Long, Long>> ids = Mono.zip(
                studentRepository.save(new Student(null, "John Doe", "example@example.example", null)).map(Student::getId),
                courseRepository.save(new Course(null, "Test title", null)).map(Course::getId)
        );

        StepVerifier.create(
                        ids.flatMap(tuple -> SCrepository.save(new StudentsCourses(tuple.getT1(), tuple.getT2()))
                                .thenMany(SCrepository.findCoursesByStudentId(tuple.getT1()))
                                .single()))
                .assertNext(course -> assertThat(course.getTitle()).isEqualTo("Test title"))
                .verifyComplete();

    }

    @Test
    @DisplayName("Test find all students by courses id")
    @DirtiesContext
    void givenCourseId_whenFindAllStudentsByCourseId_thenReturnStudents() {
        Mono<Tuple2<Long, Long>> ids = Mono.zip(
                studentRepository.save(new Student(null, "John Doe", "example@example.example", null)).map(Student::getId),
                courseRepository.save(new Course(null, "Test title", null)).map(Course::getId)
        );

        StepVerifier.create(
                        ids.flatMap(tuple -> SCrepository.save(new StudentsCourses(tuple.getT1(), tuple.getT2()))
                                .thenMany(SCrepository.findStudentsByCourseId(tuple.getT2()))
                                .single()))
                .assertNext(student -> {
                    assertThat(student.getName()).isEqualTo("John Doe");
                    assertThat(student.getEmail()).isEqualTo("example@example.example");
                })
                .verifyComplete();
    }

}