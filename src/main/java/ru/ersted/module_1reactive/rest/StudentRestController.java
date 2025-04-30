package ru.ersted.module_1reactive.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.course.CourseShortDto;
import ru.ersted.module_1reactive.dto.student.StudentDto;
import ru.ersted.module_1reactive.dto.student.rq.StudentCreateRq;
import ru.ersted.module_1reactive.dto.student.rq.StudentUpdateRq;
import ru.ersted.module_1reactive.service.CourseService;
import ru.ersted.module_1reactive.service.StudentService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentRestController {

    private final StudentService studentService;

    private final CourseService courseService;

    @PostMapping
    public Mono<ResponseEntity<StudentDto>> create(@RequestBody StudentCreateRq request) {
        return studentService.create(request).map(student ->
                ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body((student))
        );
    }

    @GetMapping
    public Flux<StudentDto> findAll() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<StudentDto> findById(@PathVariable Long id) {
        return studentService.find(id);
    }

    @PutMapping("/{id}")
    public Mono<StudentDto> update(@PathVariable Long id, @RequestBody StudentUpdateRq request) {
        return studentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Map<String, String>>> delete(@PathVariable Long id) {
        return studentService.delete(id)
                .then(Mono.just(
                                ResponseEntity
                                        .status(HttpStatus.NO_CONTENT)
                                        .body(Map.of("message", "Student deleted successfully"))
                        )
                );

    }

    @PostMapping("/{studentId}/courses/{courseId}")
    public Mono<StudentDto> addCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        return studentService.addCourse(studentId, courseId);
    }

    @GetMapping("/{studentId}/courses")
    public Flux<CourseShortDto> findCourses(@PathVariable Long studentId) {
        return courseService.findAllByStudentId(studentId);
    }

}
