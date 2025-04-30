package ru.ersted.module_1reactive.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.course.CourseDto;
import ru.ersted.module_1reactive.dto.teacher.TeacherDto;
import ru.ersted.module_1reactive.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1reactive.service.CourseService;
import ru.ersted.module_1reactive.service.TeacherService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherRestController {

    private final TeacherService teacherService;
    private final CourseService courseService;

    @PostMapping
    public Mono<ResponseEntity<TeacherDto>> create(@RequestBody TeacherCreateRq request) {
        return teacherService.create(request)
                .map(teacher ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(teacher)
                );
    }

    @PostMapping("/{teacherId}/courses/{coursesId}")
    public Mono<CourseDto> assigningTeacherToCourse(@PathVariable Long teacherId, @PathVariable Long coursesId) {
        return courseService.assigningTeacher(coursesId, teacherId);
    }

    @GetMapping
    public Flux<TeacherDto> findAll() {
        return teacherService.findAll();
    }

}
