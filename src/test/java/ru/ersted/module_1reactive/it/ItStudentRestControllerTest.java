package ru.ersted.module_1reactive.it;

import org.junit.jupiter.api.BeforeEach;
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
import ru.ersted.module_1reactive.dto.generated.StudentCreateRq;
import ru.ersted.module_1reactive.dto.generated.StudentUpdateRq;
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
class ItStudentRestControllerTest {

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


    @BeforeEach
    void setUp() {
        studentRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test create student functionality")
    @DirtiesContext
    void givenStudentCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        StudentCreateRq rq = new StudentCreateRq();
        rq.setName("John Doe");
        rq.setEmail("john.doe@example.com");

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(rq), StudentCreateRq.class)
                .exchange();

        result
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.courses").isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Test findAll student functionality")
    @DirtiesContext
    void whenFindAll_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "john.doe@example.com");
        studentRepository.save(student).block();

        Teacher teacher = new Teacher(null, "John Pohn", null);
        teacherRepository.save(teacher).block();

        Course course = new Course(null, "Math", teacher.getId());
        courseRepository.save(course).block();
        studentsCoursesRepository.save(new StudentsCourses(course.getId(), student.getId())).block();


        WebTestClient.ResponseSpec result = webClient.get().uri("/api/v1/students").exchange();


        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isNotEmpty()
                .jsonPath("$.[0].name").isEqualTo("John Doe")
                .jsonPath("$.[0].email").isEqualTo("john.doe@example.com")
                .jsonPath("$.[0].courses.[0].id").isNotEmpty()
                .jsonPath("$.[0].courses.[0].title").isEqualTo("Math")
                .jsonPath("$.[0].courses.[0].teacher.id").isNotEmpty()
                .jsonPath("$.[0].courses.[0].teacher.name").isEqualTo("John Pohn");
    }


    @Test
    @DisplayName("Test find by id student functionality")
    @DirtiesContext
    void givenStudentId_whenFindById_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "john.doe@example.com");
        studentRepository.save(student).block();

        Teacher teacher = new Teacher(null, "John Pohn", null);
        teacherRepository.save(teacher).block();

        Course course = new Course(null, "Math", teacher.getId());
        courseRepository.save(course).block();
        studentsCoursesRepository.save(new StudentsCourses(course.getId(), student.getId())).block();


        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/students/%d".formatted(student.getId()))
                .exchange();

        result.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.courses.[0].id").isNotEmpty()
                .jsonPath("$.courses.[0].title").isEqualTo("Math")
                .jsonPath("$.courses.[0].teacher.id").isNotEmpty()
                .jsonPath("$.courses.[0].teacher.name").isEqualTo("John Pohn");

    }

    @Test
    @DisplayName("Test find by id student functionality (NOT_FOUND)")
    @DirtiesContext
    void givenStudentId_whenFindById_thenNotFoundResponse() throws Exception {

        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/students/%d".formatted(1L))
                .exchange();

        result
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not found")
                .jsonPath("$.message").isEqualTo("Student with ID %d not found".formatted(1L))
                .jsonPath("$.path").isEqualTo("/api/v1/students/%d".formatted(1L));
    }

    @Test
    @DisplayName("Test update student functionality")
    @DirtiesContext
    void givenStudentUpdateRq_whenUpdate_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "Mike Noise", "mike.noise@example.com");
        studentRepository.save(student).block();

        StudentUpdateRq rq = new StudentUpdateRq();
        rq.setName("John Doe");
        rq.setEmail("john.doe@example.com");

        WebTestClient.ResponseSpec result = webClient.put()
                .uri("/api/v1/students/%d".formatted(student.getId()))
                .body(Mono.just(rq), StudentUpdateRq.class)
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()

                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.courses").isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Test delete student functionality")
    @DirtiesContext
    void givenStudentId_whenDelete_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "Mike Noise", "mike.noise@example.com");
        studentRepository.save(student).block();

        WebTestClient.ResponseSpec result = webClient.delete()
                .uri("/api/v1/students/%d".formatted(student.getId()))
                .exchange();

        result
                .expectStatus().isNoContent()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Student deleted successfully");
    }

    @Test
    @DisplayName("Test add course to student functionality")
    @DirtiesContext
    void givenStudentIdAndCourseId_whenAddCourse_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "john.doe@example.com");
        studentRepository.save(student).block();

        Teacher teacher = new Teacher(null, "John Pohn", null);
        teacherRepository.save(teacher).block();

        Course course = new Course(null, "Math", teacher.getId());
        courseRepository.save(course).block();


        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/students/%d/courses/%d".formatted(student.getId(), course.getId()))
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.courses.[0].id").isNotEmpty()
                .jsonPath("$.courses.[0].title").isEqualTo("Math")
                .jsonPath("$.courses.[0].teacher.id").isNotEmpty()
                .jsonPath("$.courses.[0].teacher.name").isEqualTo("John Pohn");
    }

    @Test
    @DisplayName("Test find all student's courses functionality")
    @DirtiesContext
    void givenStudentId_whenFindCourses_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "john.doe@example.com");
        studentRepository.save(student).block();

        Teacher teacher = new Teacher(null, "John Pohn", null);
        teacherRepository.save(teacher).block();

        Course course = new Course(null, "Math", teacher.getId());
        courseRepository.save(course).block();
        studentsCoursesRepository.save(new StudentsCourses(course.getId(), student.getId())).block();

        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/students/%d/courses".formatted(student.getId()))
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isNotEmpty()
                .jsonPath("$.[0].title").isEqualTo("Math")
                .jsonPath("$.[0].teacher.id").isNotEmpty()
                .jsonPath("$.[0].teacher.name").isEqualTo("John Pohn");
    }

}