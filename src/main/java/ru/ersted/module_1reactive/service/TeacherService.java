package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.generated.TeacherCreateRq;
import ru.ersted.module_1reactive.dto.generated.TeacherDto;
import ru.ersted.module_1reactive.entity.Teacher;
import ru.ersted.module_1reactive.mapper.TeacherMapper;
import ru.ersted.module_1reactive.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    private final TeacherEnrichmentService teacherEnrichmentService;

    private final TeacherMapper teacherMapper;


    public Mono<TeacherDto> create(TeacherCreateRq request) {
        return Mono.just(request)
                .map(teacherMapper::map)
                .flatMap(teacherRepository::save)
                .map(teacherMapper::map);
    }

    public Flux<TeacherDto> findAll() {
        return teacherRepository.findAll()
                .flatMap(teacher -> Mono.just(teacher)
                        .flatMap(teacherEnrichmentService::enrichWithCourses)
                        .flatMap(teacherEnrichmentService::enrichWithDepartment)
                )
                .map(teacherMapper::map);
    }

    public Mono<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

}
