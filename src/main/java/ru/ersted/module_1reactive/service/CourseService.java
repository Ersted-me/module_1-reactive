package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.generated.CourseCreateRq;
import ru.ersted.module_1reactive.dto.generated.CourseDto;
import ru.ersted.module_1reactive.dto.generated.CourseShortDto;
import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.mapper.CourseMapper;
import ru.ersted.module_1reactive.repository.CourseRepository;
import ru.ersted.module_1reactive.repository.StudentsCoursesRepository;


@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final StudentsCoursesRepository studentsCoursesRepository;

    private final CourseEnrichmentService courseEnrichmentService;

    private final CourseMapper courseMapper;


    public Flux<CourseShortDto> findAllByStudentId(Long studentId) {
        return studentsCoursesRepository.findCoursesByStudentId(studentId)
                .flatMap(courseEnrichmentService::enrichWithTeacher)
                .map(courseMapper::mapShort);
    }

    public Mono<CourseDto> assigningTeacher(Long coursesId, Long teacherId) {
        return courseRepository.findById(coursesId)
                .flatMap(course -> {
                    course.setTeacherId(teacherId);
                    return Mono.just(course);
                })
                .flatMap(courseRepository::save)
                .flatMap(courseEnrichmentService::enrich)
                .map(courseMapper::map);
    }

    public Mono<CourseDto> save(CourseCreateRq request) {
        return Mono.just(request)
                .map(courseMapper::map)
                .flatMap(courseRepository::save)
                .map(courseMapper::map);
    }

    public Flux<CourseDto> findAll() {
        return courseRepository.findAll()
                .flatMap(course -> Mono.just(course)
                        .flatMap(courseEnrichmentService::enrich))
                .map(courseMapper::map);
    }

    public Flux<Course> findAllByTeacherId(Long teacherId) {
        return courseRepository.findAllByTeacherId(teacherId);
    }

}
