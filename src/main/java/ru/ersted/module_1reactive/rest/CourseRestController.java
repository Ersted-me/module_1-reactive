package ru.ersted.module_1reactive.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.generated.CourseDto;
import ru.ersted.module_1reactive.dto.generated.CourseCreateRq;
import ru.ersted.module_1reactive.service.CourseService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/courses")
public class CourseRestController {

    private final CourseService courseService;

    @PostMapping
    public Mono<ResponseEntity<CourseDto>> create(@RequestBody CourseCreateRq courseDto) {
        return courseService.save(courseDto)
                .map(course ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(course)
                );
    }

    @GetMapping
    public Flux<CourseDto> getAll() {
        return courseService.findAll();
    }

}
