package ru.ersted.module_1reactive.dto.course;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.ersted.module_1reactive.dto.teacher.TeacherShortDto;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourseShortDto(Long id, String title, TeacherShortDto teacher) {
}
