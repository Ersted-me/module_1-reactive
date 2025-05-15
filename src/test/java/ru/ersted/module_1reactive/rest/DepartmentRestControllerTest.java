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
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.generated.DepartmentDto;
import ru.ersted.module_1reactive.dto.generated.DepartmentCreateRq;
import ru.ersted.module_1reactive.dto.generated.TeacherShortDto;
import ru.ersted.module_1reactive.service.DepartmentService;

@WebFluxTest(controllers = {
        DepartmentRestController.class
})
@ExtendWith(SpringExtension.class)
@ComponentScan({"ru.ersted.module_1reactive.exception"})
class DepartmentRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private DepartmentService departmentService;


    @Test
    @DisplayName("Test create department functionality")
    void givenDepartmentCreateRq_whenCreateDepartment_thenSuccessResponse() throws Exception {
        DepartmentCreateRq rq = new DepartmentCreateRq();
        rq.setName("Computer Science");

        DepartmentDto dto = new DepartmentDto();
        dto.setId(1L);
        dto.setName("Computer Science");

        BDDMockito.given(departmentService.save(rq)).willReturn(Mono.just(dto));

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(rq), DepartmentCreateRq.class)
                .exchange();

        result
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Computer Science")
                .jsonPath("$.headOfDepartment").isEmpty();
    }

    @Test
    @DisplayName("Test assigning teacher to departament functionality")
    void givenDepartmentIdAndTeacherId_whenAssign_thenSuccessResponse() throws Exception {
        Long departmentId = 1L;
        Long teacherId = 1L;
        TeacherShortDto teacher = new TeacherShortDto();
        teacher.setId(teacherId);
        teacher.setName("Professor Smith");

        DepartmentDto dto = new DepartmentDto();
        dto.setId(departmentId);
        dto.setName("Computer Science");
        dto.setHeadOfDepartment(teacher);

        BDDMockito.given(departmentService.assigningHeadOfDepartment(departmentId, teacherId)).willReturn(Mono.just(dto));

        WebTestClient.ResponseSpec result = webClient.post()
                .uri("/api/v1/departments/%d/teacher/%d".formatted(departmentId, teacherId))
                .exchange();

        result
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Computer Science")
                .jsonPath("$.headOfDepartment.id").isEqualTo(1)
                .jsonPath("$.headOfDepartment.name").isEqualTo("Professor Smith");

    }

}