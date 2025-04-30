package ru.ersted.module_1reactive.mapper.repository;


import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.entity.Department;
import ru.ersted.module_1reactive.entity.Teacher;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TeacherRowMapper {

    public record TeacherRowData(
            Long teacherId, String teacherName,
            Long departmentId, String departmentName,
            Long courseId, String courseTitle) {
    }

    public static Teacher buildTeacher(List<TeacherRowData> rows) {

        TeacherRowData first = rows.getFirst();
        Teacher teacher = new Teacher(first.teacherId(), first.teacherName(), null);

        if (first.departmentId() != null) {
            teacher.setDepartment(new Department(first.departmentId(), first.departmentName(), null));
        }

        Set<Course> courses = rows.stream()
                .filter(r -> r.courseId() != null)
                .map(r -> new Course(r.courseId(), r.courseTitle(), teacher.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        teacher.setCourses(new HashSet<>(courses));
        return teacher;
    }

}
