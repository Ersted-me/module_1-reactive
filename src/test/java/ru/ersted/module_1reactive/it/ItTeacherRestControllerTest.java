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
import ru.ersted.module_1reactive.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1reactive.entity.*;
import ru.ersted.module_1reactive.repository.*;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({DatabaseConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Testcontainers
class ItTeacherRestControllerTest {


    @Autowired
    private WebTestClient webClient;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudentsCoursesRepository studentsCoursesRepository;


    @Test
    @DisplayName("Test create teacher functionality")
    @DirtiesContext
    void givenTeacherCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        TeacherCreateRq rq = new TeacherCreateRq("Professor Smith");

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(rq), TeacherCreateRq.class)
                .exchange();

        result
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Professor Smith")
                .jsonPath("$.courses").isEqualTo(Collections.emptyList())
                .jsonPath("$.department").isEmpty();
    }

    @Test
    @DisplayName("Test assigning teacher to course functionality")
    @DirtiesContext
    void givenTeacherIdAndCourseId_whenAssigningTeacherToCourse_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "john.doe@example.com");
        studentRepository.save(student).block();

        Teacher teacher = new Teacher(null, "Professor Smith", null);
        teacherRepository.save(teacher).block();

        Course course = new Course(null, "Math 101", teacher.getId());
        courseRepository.save(course).block();
        studentsCoursesRepository.save(new StudentsCourses(course.getId(), student.getId())).block();


        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/teachers/%d/courses/%d".formatted(teacher.getId(), course.getId()))
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo("Math 101")
                .jsonPath("$.teacher.id").isNotEmpty()
                .jsonPath("$.teacher.name").isEqualTo("Professor Smith")
                .jsonPath("$.students.[0].id").isNotEmpty()
                .jsonPath("$.students.[0].name").isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Test find all teachers functionality")
    @DirtiesContext
    void whenFindAll_thenSuccessResponse() throws Exception {
        Student student = new Student(null, "John Doe", "john.doe@example.com");
        studentRepository.save(student).block();

        Department department = new Department(null, "Computer Science", null);
        departmentRepository.save(department).block();

        Teacher teacher = new Teacher(null, "Professor Smith", department.getId());
        teacherRepository.save(teacher).block();

        Course course = new Course(null, "Math 101", teacher.getId());
        courseRepository.save(course).block();
        studentsCoursesRepository.save(new StudentsCourses(course.getId(), student.getId())).block();


        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/teachers")
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isNotEmpty()
                .jsonPath("$.[0].name").isEqualTo("Professor Smith")
                .jsonPath("$.[0].courses.[0].id").isNotEmpty()
                .jsonPath("$.[0].courses.[0].title").isEqualTo("Math 101")
                .jsonPath("$.[0].department.id").isNotEmpty()
                .jsonPath("$.[0].department.name").isEqualTo("Computer Science");
    }

}