package ru.ersted.module_1reactive.dto.department;


import ru.ersted.module_1reactive.dto.teacher.TeacherShortDto;

public record DepartmentDto(Long id, String name, TeacherShortDto headOfDepartment) {
}
