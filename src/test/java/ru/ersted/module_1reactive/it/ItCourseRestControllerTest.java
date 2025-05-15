package ru.ersted.module_1reactive.it;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.config.DatabaseConfig;
import ru.ersted.module_1reactive.dto.generated.CourseCreateRq;
import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.entity.Student;
import ru.ersted.module_1reactive.entity.StudentsCourses;
import ru.ersted.module_1reactive.entity.Teacher;
import ru.ersted.module_1reactive.repository.CourseRepository;
import ru.ersted.module_1reactive.repository.StudentRepository;
import ru.ersted.module_1reactive.repository.StudentsCoursesRepository;
import ru.ersted.module_1reactive.repository.TeacherRepository;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({DatabaseConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Testcontainers
class ItCourseRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentsCoursesRepository studentsCoursesRepository;


    @Test
    @DisplayName("Test create course functionality")
    @DirtiesContext
    void givenCourseCreteRq_whenCreateCourse_thenSuccessResponse() throws Exception {
        CourseCreateRq rq = new CourseCreateRq();
        rq.setTitle("Math");

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(rq), CourseCreateRq.class)
                .exchange();

        result
                .expectStatus().isCreated()
                .expectBody()

                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Math")
                .jsonPath("$.teacher").isEmpty()
                .jsonPath("$.students").isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Test find all courses functionality")
    @DirtiesContext
    void whenFindAll_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "john.doe@example.com");
        studentRepository.save(student).block();

        Teacher teacher = new Teacher(null, "Professor Smith", null);
        teacherRepository.save(teacher).block();

        Course course = new Course(null, "Math", teacher.getId());
        courseRepository.save(course).block();
        studentsCoursesRepository.save(new StudentsCourses(course.getId(), student.getId())).block();

        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/courses")
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isNotEmpty()
                .jsonPath("$.[0].title").isEqualTo("Math")
                .jsonPath("$.[0].teacher.id").isNotEmpty()
                .jsonPath("$.[0].teacher.name").isEqualTo("Professor Smith")
                .jsonPath("$.[0].students.[0].id").isNotEmpty()
                .jsonPath("$.[0].students.[0].name").isEqualTo("John Doe");
    }

}