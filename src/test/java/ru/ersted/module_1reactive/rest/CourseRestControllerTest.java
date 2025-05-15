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
import ru.ersted.module_1reactive.dto.generated.CourseDto;
import ru.ersted.module_1reactive.dto.generated.CourseCreateRq;
import ru.ersted.module_1reactive.dto.generated.StudentShortDto;
import ru.ersted.module_1reactive.dto.generated.TeacherShortDto;
import ru.ersted.module_1reactive.service.CourseService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@WebFluxTest(controllers = {
        CourseRestController.class
})
@ExtendWith(SpringExtension.class)
@ComponentScan({"ru.ersted.module_1reactive.exception"})
class CourseRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private CourseService courseService;


    @Test
    @DisplayName("Test create course functionality")
    void givenCourseCreteRq_whenCreateCourse_thenSuccessResponse() throws Exception {
        CourseCreateRq rq = new CourseCreateRq();
        rq.setTitle("Math");

        CourseDto dto = new CourseDto();
        dto.setId(1L);
        dto.setTitle("Math");

        BDDMockito.given(courseService.save(rq)).willReturn(Mono.just(dto));

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
    void whenFindAll_thenSuccessResponse() throws Exception {
        TeacherShortDto teacher = new TeacherShortDto();
        teacher.setId(1L);
        teacher.setName("Professor Smith");

        StudentShortDto student = new StudentShortDto();
        student.setId(1L);
        student.setName("John Doe");

        CourseDto course = new CourseDto();
        course.setId(1L);
        course.setTitle("Math");
        course.setTeacher(teacher);
        course.setStudents(Set.of(student));

        List<CourseDto> list = Collections.singletonList(course);

        BDDMockito.given(courseService.findAll()).willReturn(Flux.fromIterable(list));

        WebTestClient.ResponseSpec result = webClient.get()
                .uri("/api/v1/courses")
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(1)
                .jsonPath("$.[0].title").isEqualTo("Math")
                .jsonPath("$.[0].teacher.id").isEqualTo(1)
                .jsonPath("$.[0].teacher.name").isEqualTo("Professor Smith")
                .jsonPath("$.[0].students.[0].id").isEqualTo(1)
                .jsonPath("$.[0].students.[0].name").isEqualTo("John Doe");
    }

}