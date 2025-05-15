package ru.ersted.module_1reactive.service;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.entity.Student;
import ru.ersted.module_1reactive.repository.StudentsCoursesRepository;

import java.util.HashSet;

@Service
public class StudentEnrichmentService {

    private final StudentsCoursesRepository studentsCoursesRepository;

    private final CourseEnrichmentService courseEnrichmentService;


    public StudentEnrichmentService(StudentsCoursesRepository studentsCoursesRepository,
                                    @Lazy CourseEnrichmentService courseEnrichmentService) {
        this.studentsCoursesRepository = studentsCoursesRepository;
        this.courseEnrichmentService = courseEnrichmentService;
    }

    public Mono<Student> enrichWithCourses(Student student) {
        return studentsCoursesRepository.findCoursesByStudentId(student.getId())
                .flatMap(courseEnrichmentService::enrichWithTeacher)
                .collectList()
                .flatMap(courses -> {
                    student.setCourses(new HashSet<>(courses));
                    return Mono.just(student);
                });
    }

}
