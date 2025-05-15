package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.repository.StudentsCoursesRepository;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CourseEnrichmentService {

    private final TeacherService teacherService;

    private final StudentsCoursesRepository studentsCoursesRepository;


    public Mono<Course> enrich(Course course) {
        return Mono.just(course)
                .flatMap(this::enrichWithTeacher)
                .flatMap(this::enrichWithStudents);
    }


    public Mono<Course> enrichWithTeacher(Course course) {
        return teacherService.findById(course.getTeacherId())
                .map(teacher -> {
                    course.setTeacher(teacher);
                    return course;
                });
    }

    public Mono<Course> enrichWithStudents(Course course) {
        return studentsCoursesRepository.findStudentsByCourseId(course.getId())
                .collectList()
                .map(students -> {
                    course.setStudents(new HashSet<>(students));
                    return course;
                });
    }

}
