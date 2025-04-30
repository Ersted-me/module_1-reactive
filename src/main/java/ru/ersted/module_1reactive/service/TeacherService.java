package ru.ersted.module_1reactive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.dto.teacher.TeacherDto;
import ru.ersted.module_1reactive.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1reactive.mapper.TeacherMapper;
import ru.ersted.module_1reactive.repository.TeacherRepository;
import ru.ersted.module_1reactive.repository.search.TeacherDeepFetchRepository;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherDeepFetchRepository teacherDeepFetchRepository;
    private final TeacherMapper teacherMapper;

    public Mono<TeacherDto> create(TeacherCreateRq request) {
        return Mono.just(request)
                .map(teacherMapper::map)
                .flatMap(teacherRepository::save)
                .map(teacherMapper::map);
    }

    public Flux<TeacherDto> findAll() {
        return teacherDeepFetchRepository.findAllWithDepartmentAndCourses()
                .map(teacherMapper::map);
    }

}
