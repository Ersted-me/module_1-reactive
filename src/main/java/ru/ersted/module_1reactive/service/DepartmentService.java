package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.department.DepartmentDto;
import ru.ersted.module_1reactive.dto.department.rq.DepartmentCreateRq;
import ru.ersted.module_1reactive.mapper.DepartmentMapper;
import ru.ersted.module_1reactive.repository.DepartmentRepository;
import ru.ersted.module_1reactive.repository.search.DepartmentDeepFetchRepository;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final DepartmentDeepFetchRepository departmentDeepFetchRepository;

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
                .flatMap(department -> departmentDeepFetchRepository.findByDepartmentId(departmentId))
                .map(departmentMapper::map);
    }
}
