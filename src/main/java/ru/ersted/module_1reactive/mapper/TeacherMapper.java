package ru.ersted.module_1reactive.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1reactive.dto.teacher.TeacherDto;
import ru.ersted.module_1reactive.dto.teacher.TeacherShortDto;
import ru.ersted.module_1reactive.dto.teacher.rq.TeacherCreateRq;
import ru.ersted.module_1reactive.entity.Teacher;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    Teacher map(TeacherCreateRq request);

    TeacherDto map(Teacher teacher);

    TeacherShortDto mapShort(Teacher teacher);

}
