package ru.ersted.module_1reactive.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.course.CourseShortDto;
import ru.ersted.module_1reactive.dto.student.StudentDto;
import ru.ersted.module_1reactive.dto.student.rq.StudentCreateRq;
import ru.ersted.module_1reactive.dto.student.rq.StudentUpdateRq;
import ru.ersted.module_1reactive.dto.teacher.TeacherShortDto;
import ru.ersted.module_1reactive.exception.NotFoundException;
import ru.ersted.module_1reactive.service.CourseService;
import ru.ersted.module_1reactive.service.StudentService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = {
        StudentRestController.class
})
@ExtendWith(SpringExtension.class)
@ComponentScan({"ru.ersted.module_1reactive.exception"})
class StudentRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private CourseService courseService;

    @Test
    @DisplayName("Test create student functionality")
    void givenStudentCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        StudentCreateRq rq = new StudentCreateRq("John Doe", "john.doe@example.com");
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com", null);

        BDDMockito.given(studentService.create(any(StudentCreateRq.class)))
                .willReturn(Mono.just(dto));


        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(rq), StudentCreateRq.class)
                .exchange();


        result
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.courses").isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Test findAll student functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        List<StudentDto> list = List.of(new StudentDto(1L, "John Doe", "john.doe@example.com",
                Set.of(new CourseShortDto(1L, "Math", new TeacherShortDto(1L, "John Pohn")))));

        BDDMockito.given(studentService.findAll())
                .willReturn(Flux.fromIterable(list));

        WebTestClient.ResponseSpec result = webClient.get().uri("/api/v1/students").exchange();


        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(1)
                .jsonPath("$.[0].name").isEqualTo("John Doe")
                .jsonPath("$.[0].email").isEqualTo("john.doe@example.com")
                .jsonPath("$.[0].courses.[0].id").isEqualTo(1)
                .jsonPath("$.[0].courses.[0].title").isEqualTo("Math")
                .jsonPath("$.[0].courses.[0].teacher.id").isEqualTo(1)
                .jsonPath("$.[0].courses.[0].teacher.name").isEqualTo("John Pohn");
    }

    @Test
    @DisplayName("Test find by id student functionality")
    void givenStudentId_whenFindById_thenSuccessResponse() throws Exception {
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com",
                Set.of(new CourseShortDto(1L, "Math", new TeacherShortDto(1L, "John Pohn"))));
        Long studentId = 1L;

        BDDMockito.given(studentService.find(studentId))
                .willReturn(Mono.just(dto));


        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/students/%d".formatted(studentId))
                .exchange();

        result.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.courses.[0].id").isEqualTo(1)
                .jsonPath("$.courses.[0].title").isEqualTo("Math")
                .jsonPath("$.courses.[0].teacher.id").isEqualTo(1)
                .jsonPath("$.courses.[0].teacher.name").isEqualTo("John Pohn");

    }

    @Test
    @DisplayName("Test find by id student functionality (NOT_FOUND)")
    void givenStudentId_whenFindById_thenNotFoundResponse() throws Exception {

        Long studentId = 1L;

        BDDMockito.given(studentService.find(studentId))
                .willThrow(new NotFoundException("Student with ID %d not found".formatted(studentId)));

        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/students/%d".formatted(studentId))
                .exchange();

        result
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not found")
                .jsonPath("$.message").isEqualTo("Student with ID %d not found".formatted(studentId))
                .jsonPath("$.path").isEqualTo("/api/v1/students/%d".formatted(studentId));
    }

    @Test
    @DisplayName("Test update student functionality")
    void givenStudentUpdateRq_whenUpdate_thenSuccessResponse() throws Exception {
        StudentUpdateRq rq = new StudentUpdateRq("John Doe", "john.doe@example.com");
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com", null);

        Long studentId = 1L;

        BDDMockito.given(studentService.update(studentId, rq))
                .willReturn(Mono.just(dto));

        WebTestClient.ResponseSpec result = webClient.put()
                .uri("/api/v1/students/%d".formatted(studentId))
                .body(Mono.just(rq), StudentUpdateRq.class)
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()

                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.courses").isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("Test delete student functionality")
    void givenStudentId_whenDelete_thenSuccessResponse() throws Exception {
        Long studentId = 1L;

        BDDMockito.when(studentService.delete(studentId)).thenReturn(Mono.empty());

        WebTestClient.ResponseSpec result = webClient.delete()
                .uri("/api/v1/students/%d".formatted(studentId))
                .exchange();

        result
                .expectStatus().isNoContent()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Student deleted successfully");
    }

    @Test
    @DisplayName("Test add course to student functionality")
    void givenStudentIdAndCourseId_whenAddCourse_thenSuccessResponse() throws Exception {
        Long studentId = 1L;
        Long courseId = 1L;
        StudentDto dto = new StudentDto(1L, "John Doe", "john.doe@example.com",
                Set.of(new CourseShortDto(1L, "Math", new TeacherShortDto(1L, "John Pohn"))));

        BDDMockito.given(studentService.addCourse(courseId, studentId)).willReturn(Mono.just(dto));

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/students/%d/courses/%d".formatted(studentId, courseId))
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.courses.[0].id").isEqualTo(1)
                .jsonPath("$.courses.[0].title").isEqualTo("Math")
                .jsonPath("$.courses.[0].teacher.id").isEqualTo(1)
                .jsonPath("$.courses.[0].teacher.name").isEqualTo("John Pohn");
    }

    @Test
    @DisplayName("Test find all student's courses functionality")
    void givenStudentId_whenFindCourses_thenSuccessResponse() throws Exception {
        Long studentId = 1L;
        Set<CourseShortDto> courses = Set.of(new CourseShortDto(1L, "Math", new TeacherShortDto(1L, "John Pohn")));

        BDDMockito.given(courseService.findAllByStudentId(studentId)).willReturn(Flux.fromIterable(courses));

        WebTestClient.ResponseSpec result = webClient.get().uri("/api/v1/students/%d/courses".formatted(studentId)).exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(1)
                .jsonPath("$.[0].title").isEqualTo("Math")
                .jsonPath("$.[0].teacher.id").isEqualTo(1)
                .jsonPath("$.[0].teacher.name").isEqualTo("John Pohn");
    }

}