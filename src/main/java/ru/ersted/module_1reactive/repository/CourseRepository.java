package ru.ersted.module_1reactive.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.entity.Course;

public interface CourseRepository extends ReactiveCrudRepository<Course, Long> {
    Flux<Course> findAllByTeacherId(Long teacherId);
}
