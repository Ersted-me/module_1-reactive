package ru.ersted.module_1reactive.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1reactive.dto.generated.TeacherDto;
import ru.ersted.module_1reactive.dto.generated.TeacherShortDto;
import ru.ersted.module_1reactive.dto.generated.TeacherCreateRq;
import ru.ersted.module_1reactive.entity.Teacher;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    Teacher map(TeacherCreateRq request);

    TeacherDto map(Teacher teacher);

    TeacherShortDto mapShort(Teacher teacher);

}
