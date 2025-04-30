package ru.ersted.module_1reactive.mapper;

import org.mapstruct.Mapper;
import ru.ersted.module_1reactive.dto.department.DepartmentDto;
import ru.ersted.module_1reactive.dto.department.DepartmentShortDto;
import ru.ersted.module_1reactive.dto.department.rq.DepartmentCreateRq;
import ru.ersted.module_1reactive.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department map(DepartmentCreateRq request);

    DepartmentDto map(Department department);

    DepartmentShortDto mapShort(Department department);

}
