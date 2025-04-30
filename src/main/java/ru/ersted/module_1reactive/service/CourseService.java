package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.course.CourseDto;
import ru.ersted.module_1reactive.dto.course.CourseShortDto;
import ru.ersted.module_1reactive.dto.course.rq.CourseCreateRq;
import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.mapper.CourseMapper;
import ru.ersted.module_1reactive.repository.CourseRepository;
import ru.ersted.module_1reactive.repository.StudentsCoursesRepository;
import ru.ersted.module_1reactive.repository.search.CourseDeepFetchRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentsCoursesRepository studentsCoursesRepository;
    private final CourseDeepFetchRepository courseDeepFetchRepository;

    private final CourseMapper courseMapper;

    public Flux<CourseShortDto> findAllByStudentId(Long studentId) {
        return courseDeepFetchRepository.findByStudentId(studentId)
                .map(courseMapper::mapShort);
    }

    public Mono<CourseDto> assigningTeacher(Long coursesId, Long teacherId) {
        return courseRepository.findById(coursesId)
                .flatMap(course -> {
                    course.setTeacherId(teacherId);
                    return courseRepository.save(course);
                })
                .flatMap(course -> courseDeepFetchRepository.findByCourseId(course.getId()))
                .map(courseMapper::map);
    }

    public Mono<CourseDto> save(CourseCreateRq request) {
        return Mono.just(request)
                .map(courseMapper::map)
                .flatMap(courseRepository::save)
                .map(courseMapper::map);
    }

    public Flux<CourseDto> findAll() {
        return courseDeepFetchRepository.findAll()
                .map(courseMapper::map);
    }

}
