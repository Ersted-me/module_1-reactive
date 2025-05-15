package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.generated.DepartmentCreateRq;
import ru.ersted.module_1reactive.dto.generated.DepartmentDto;
import ru.ersted.module_1reactive.entity.Department;
import ru.ersted.module_1reactive.mapper.DepartmentMapper;
import ru.ersted.module_1reactive.repository.DepartmentRepository;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper;

    private final DepartmentEnrichmentService departmentEnrichmentService;


    public Mono<DepartmentDto> save(DepartmentCreateRq request) {
        return Mono.just(request)
                .map(departmentMapper::map)
                .flatMap(departmentRepository::save)
                .map(departmentMapper::map);
    }

    public Mono<DepartmentDto> assigningHeadOfDepartment(Long departmentId, Long teacherId) {
        return departmentRepository.findById(departmentId)
                .flatMap(department -> {
                    department.setHeadOfDepartmentId(teacherId);
                    return departmentRepository.save(department);
                })
                .flatMap(departmentEnrichmentService::enrichWithHeadOfDepartment)
                .map(departmentMapper::map);
    }

    public Mono<Department> findById(Long departmentId) {
        return departmentRepository.findById(departmentId);
    }

}
