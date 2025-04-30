package ru.ersted.module_1reactive.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1reactive.dto.course.CourseDto;
import ru.ersted.module_1reactive.dto.course.CourseShortDto;
import ru.ersted.module_1reactive.dto.course.rq.CourseCreateRq;
import ru.ersted.module_1reactive.entity.Course;


@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDto map(Course entity);

    CourseShortDto mapShort(Course entity);

    Course map(CourseCreateRq request);

}
