package ru.ersted.module_1reactive.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.entity.Teacher;

import java.util.HashSet;

@Service
public class TeacherEnrichmentService {

    private final CourseService courseService;

    private final DepartmentService departmentService;

    public TeacherEnrichmentService(@Lazy CourseService courseService, @Lazy DepartmentService departmentService) {
        this.courseService = courseService;
        this.departmentService = departmentService;
    }

    public Mono<Teacher> enrichWithDepartment(Teacher teacher) {
        return departmentService.findById(teacher.getDepartmentId())
                .map(department -> {
                    teacher.setDepartment(department);
                    return teacher;
                });
    }

    public Mono<Teacher> enrichWithCourses(Teacher teacher) {
        return courseService.findAllByTeacherId(teacher.getId())
                .collectList()
                .map(courses -> {
                    teacher.setCourses(new HashSet<>(courses));
                    return teacher;
                });
    }

}
