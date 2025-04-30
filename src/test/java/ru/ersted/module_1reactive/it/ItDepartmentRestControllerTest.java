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
import ru.ersted.module_1reactive.dto.department.rq.DepartmentCreateRq;
import ru.ersted.module_1reactive.entity.Department;
import ru.ersted.module_1reactive.entity.Teacher;
import ru.ersted.module_1reactive.repository.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({DatabaseConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Testcontainers
class ItDepartmentRestControllerTest {

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
    @DisplayName("Test create department functionality")
    @DirtiesContext
    void givenDepartmentCreateRq_whenCreateDepartment_thenSuccessResponse() throws Exception {
        DepartmentCreateRq rq = new DepartmentCreateRq("Computer Science");

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(rq), DepartmentCreateRq.class)
                .exchange();

        result
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Computer Science")
                .jsonPath("$.headOfDepartment").isEmpty();
    }

    @Test
    @DisplayName("Test assigning teacher to departament functionality")
    @DirtiesContext
    void givenDepartmentIdAndTeacherId_whenAssign_thenSuccessResponse() throws Exception {

        Teacher teacher = new Teacher(null, "Professor Smith", null);
        teacherRepository.save(teacher).block();

        Department department = new Department(null, "Computer Science", teacher.getId());
        departmentRepository.save(department).block();

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/departments/%d/teacher/%d".formatted(department.getId(), teacher.getId()))
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Computer Science")
                .jsonPath("$.headOfDepartment.id").isNotEmpty()
                .jsonPath("$.headOfDepartment.name").isEqualTo("Professor Smith");
    }

}