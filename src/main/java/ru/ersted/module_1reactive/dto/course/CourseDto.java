package ru.ersted.module_1reactive.dto.course;



import ru.ersted.module_1reactive.dto.student.StudentShortDto;
import ru.ersted.module_1reactive.dto.teacher.TeacherShortDto;

import java.util.Optional;
import java.util.Set;

public record CourseDto(Long id, String title, TeacherShortDto teacher, Set<StudentShortDto> students) {

    @Override
    public Set<StudentShortDto> students() {
        return Optional.ofNullable(students).orElse(Set.of());
    }

}
