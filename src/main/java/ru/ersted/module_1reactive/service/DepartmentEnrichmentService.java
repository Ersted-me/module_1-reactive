package ru.ersted.module_1reactive.service;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.entity.Department;

@Service
public class DepartmentEnrichmentService {

    private final TeacherService teacherService;

    public DepartmentEnrichmentService(@Lazy TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public Mono<Department> enrichWithHeadOfDepartment(Department department) {
        return teacherService.findById(department.getHeadOfDepartmentId())
                .map(teacher -> {
                    department.setHeadOfDepartment(teacher);
                    return department;
                });
    }

}
