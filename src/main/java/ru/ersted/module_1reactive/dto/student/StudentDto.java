package ru.ersted.module_1reactive.dto.student;

import ru.ersted.module_1reactive.dto.course.CourseShortDto;

import java.util.Optional;
import java.util.Set;

public record StudentDto(Long id, String name, String email, Set<CourseShortDto> courses) {

    @Override
    public Set<CourseShortDto> courses() {
        return Optional.ofNullable(courses).orElse(Set.of());
    }

}
