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
import ru.ersted.module_1reactive.dto.generated.CourseBasicDto;
import ru.ersted.module_1reactive.dto.generated.CourseDto;
import ru.ersted.module_1reactive.dto.generated.DepartmentShortDto;
import ru.ersted.module_1reactive.dto.generated.StudentShortDto;
import ru.ersted.module_1reactive.dto.generated.TeacherCreateRq;
import ru.ersted.module_1reactive.dto.generated.TeacherDto;
import ru.ersted.module_1reactive.dto.generated.TeacherShortDto;
import ru.ersted.module_1reactive.service.CourseService;
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
        TeacherCreateRq rq = new TeacherCreateRq();
        rq.setName("Professor Smith");

        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setName("Professor Smith");

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
        TeacherShortDto teacher = new TeacherShortDto();
        teacher.setId(teacherId);
        teacher.setName("Professor Smith");

        StudentShortDto student = new StudentShortDto();
        student.setId(courseId);
        student.setName("John Doe");

        CourseDto courseDto = new CourseDto();
        courseDto.setId(courseId);
        courseDto.setTitle("Math 101");
        courseDto.setTeacher(teacher);
        courseDto.setStudents(Set.of(student));

        BDDMockito.given(courseService.assigningTeacher(courseId, teacherId))
                .willReturn(Mono.just(courseDto));

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
        CourseBasicDto course = new CourseBasicDto();
        course.setId(1L);
        course.setTitle("Math 101");

        DepartmentShortDto department = new DepartmentShortDto();
        department.setId(1L);
        department.setName("Computer Science");

        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setName("Professor Smith");
        dto.setDepartment(department);
        dto.setCourses(Set.of(course));

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