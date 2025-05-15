package ru.ersted.module_1reactive.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.generated.DepartmentDto;
import ru.ersted.module_1reactive.dto.generated.DepartmentCreateRq;
import ru.ersted.module_1reactive.service.DepartmentService;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentRestController {

    private final DepartmentService departmentService;

    @PostMapping
    public Mono<ResponseEntity<DepartmentDto>> create(@RequestBody DepartmentCreateRq request) {
        return departmentService.save(request)
                .map(department ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(department)
                );
    }

    @PostMapping("/{departmentId}/teacher/{teacherId}")
    public Mono<DepartmentDto> assigningHeadOfDepartment(@PathVariable Long departmentId, @PathVariable Long teacherId) {
        return departmentService.assigningHeadOfDepartment(departmentId, teacherId);
    }

}
