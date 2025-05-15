package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.generated.StudentCreateRq;
import ru.ersted.module_1reactive.dto.generated.StudentDto;
import ru.ersted.module_1reactive.dto.generated.StudentUpdateRq;
import ru.ersted.module_1reactive.entity.StudentsCourses;
import ru.ersted.module_1reactive.exception.NotFoundException;
import ru.ersted.module_1reactive.mapper.StudentMapper;
import ru.ersted.module_1reactive.repository.StudentRepository;
import ru.ersted.module_1reactive.repository.StudentsCoursesRepository;


@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentsCoursesRepository studentsCoursesRepository;

    private final StudentMapper studentMapper;

    private final StudentEnrichmentService studentEnrichmentService;


    public Mono<StudentDto> create(StudentCreateRq request) {
        return Mono.just(request)
                .map(studentMapper::map)
                .flatMap(studentRepository::save)
                .map(studentMapper::map);
    }

    public Flux<StudentDto> findAll() {
        return studentRepository.findAll()
                .flatMap(student -> Mono.just(student)
                        .flatMap(studentEnrichmentService::enrichWithCourses)
                )
                .map(studentMapper::map);
    }

    public Mono<StudentDto> find(Long id) {
        return studentRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Student with ID %d not found".formatted(id))))
                .flatMap(studentEnrichmentService::enrichWithCourses)
                .map(studentMapper::map);
    }

    public Mono<StudentDto> update(Long id, StudentUpdateRq request) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setName(request.getName());
                    student.setEmail(request.getEmail());
                    return student;
                })
                .map(studentMapper::map);
    }

    public Mono<Void> delete(Long id) {
        return studentRepository.deleteById(id);
    }

    public Mono<StudentDto> addCourse(Long studentId, Long courseId) {
        return studentsCoursesRepository.save(new StudentsCourses(studentId, courseId))
                .then(studentRepository.findById(studentId))
                .flatMap(studentEnrichmentService::enrichWithCourses)
                .map(studentMapper::map);
    }

}
