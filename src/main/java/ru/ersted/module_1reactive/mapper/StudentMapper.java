package ru.ersted.module_1reactive.mapper;


import org.mapstruct.Mapper;
import ru.ersted.module_1reactive.dto.student.StudentDto;
import ru.ersted.module_1reactive.dto.student.rq.StudentCreateRq;
import ru.ersted.module_1reactive.entity.Student;


@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student map(StudentCreateRq request);

    StudentDto map(Student entity);

}
