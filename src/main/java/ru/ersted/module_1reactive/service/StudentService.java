package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.student.StudentDto;
import ru.ersted.module_1reactive.dto.student.rq.StudentCreateRq;
import ru.ersted.module_1reactive.dto.student.rq.StudentUpdateRq;
import ru.ersted.module_1reactive.entity.StudentsCourses;
import ru.ersted.module_1reactive.exception.NotFoundException;
import ru.ersted.module_1reactive.mapper.StudentMapper;
import ru.ersted.module_1reactive.repository.StudentRepository;
import ru.ersted.module_1reactive.repository.StudentsCoursesRepository;
import ru.ersted.module_1reactive.repository.search.StudentDeepFetchRepository;


@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentsCoursesRepository studentsCoursesRepository;
    private final StudentDeepFetchRepository studentDeepFetchRepository;
    private final StudentMapper studentMapper;

    public Mono<StudentDto> create(StudentCreateRq request) {
        return Mono.just(request)
                .map(studentMapper::map)
                .flatMap(studentRepository::save)
                .map(studentMapper::map);
    }

    public Flux<StudentDto> findAll() {
        return studentDeepFetchRepository.findAll()
                .map(studentMapper::map);
    }

    public Mono<StudentDto> find(Long id) {
        return studentDeepFetchRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Student with ID %d not found".formatted(id))))
                .map(studentMapper::map);
    }

    public Mono<StudentDto> update(Long id, StudentUpdateRq request) {
        return studentRepository.findById(id)
                .map(student -> student.update(request))
                .map(studentMapper::map);
    }

    public Mono<Void> delete(Long id) {
        return studentRepository.deleteById(id);
    }

    public Mono<StudentDto> addCourse(Long studentId, Long courseId) {
        return studentsCoursesRepository.save(new StudentsCourses(studentId, courseId))
                .then(studentDeepFetchRepository.findById(studentId))
                .map(studentMapper::map);
    }

}
