package ru.ersted.module_1reactive.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.course.CourseDto;
import ru.ersted.module_1reactive.dto.course.CourseShortDto;
import ru.ersted.module_1reactive.dto.department.DepartmentShortDto;
import ru.ersted.module_1reactive.dto.student.StudentShortDto;
import ru.ersted.module_1reactive.dto.teacher.TeacherDto;
import ru.ersted.module_1reactive.dto.teacher.TeacherShortDto;
import ru.ersted.module_1reactive.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1reactive.service.CourseService;
import ru.ersted.module_1reactive.service.StudentService;
import ru.ersted.module_1reactive.service.TeacherService;

import java.util.Collections;
import java.util.Set;

@WebFluxTest(controllers = {
        TeacherRestController.class
})
@ExtendWith(SpringExtension.class)
@ComponentScan({"ru.ersted.module_1reactive.exception"})
class TeacherRestControllerTest {


    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private CourseService courseService;

    @Test
    @DisplayName("Test create teacher functionality")
    void givenTeacherCreateRq_whenCreate_thenSuccessResponse() throws Exception {
        TeacherCreateRq rq = new TeacherCreateRq("Professor Smith");
        TeacherDto dto = new TeacherDto(1L, "Professor Smith", null, null);

        BDDMockito.given(teacherService.create(rq))
                .willReturn(Mono.just(dto));

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(rq), TeacherCreateRq.class)
                .exchange();

        result
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Professor Smith")
                .jsonPath("$.courses").isEqualTo(Collections.emptyList())
                .jsonPath("$.department").isEmpty();
    }

    @Test
    @DisplayName("Test assigning teacher to course functionality")
    void givenTeacherIdAndCourseId_whenAssigningTeacherToCourse_thenSuccessResponse() throws Exception {
        Long teacherId = 1L;
        Long courseId = 1L;
        TeacherShortDto teacher = new TeacherShortDto(1L, "Professor Smith");
        StudentShortDto student = new StudentShortDto(1L, "John Doe");
        CourseDto courseDto = new CourseDto(1L, "Math 101", teacher, Set.of(student));

        BDDMockito.given(courseService.assigningTeacher(courseId, teacherId)).willReturn(Mono.just(courseDto));

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/teachers/%d/courses/%d".formatted(teacherId, courseId))
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Math 101")
                .jsonPath("$.teacher.id").isEqualTo(1)
                .jsonPath("$.teacher.name").isEqualTo("Professor Smith")
                .jsonPath("$.students.[0].id").isEqualTo(1)
                .jsonPath("$.students.[0].name").isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Test find all teachers functionality")
    void whenFindAll_thenSuccessResponse() throws Exception {
        CourseShortDto course = new CourseShortDto(1L, "Math 101", null);
        DepartmentShortDto department = new DepartmentShortDto(1L, "Computer Science");
        TeacherDto dto = new TeacherDto(1L, "Professor Smith", Set.of(course), department);

        BDDMockito.given(teacherService.findAll()).willReturn(Flux.just(dto));

        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/teachers")
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(1)
                .jsonPath("$.[0].name").isEqualTo("Professor Smith")
                .jsonPath("$.[0].courses.[0].id").isEqualTo(1)
                .jsonPath("$.[0].courses.[0].title").isEqualTo("Math 101")
                .jsonPath("$.[0].department.id").isEqualTo(1)
                .jsonPath("$.[0].department.name").isEqualTo("Computer Science");
    }

}