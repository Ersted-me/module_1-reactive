package ru.ersted.module_1reactive.mapper.repository;


import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.entity.Student;
import ru.ersted.module_1reactive.entity.Teacher;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseRowMapper {

    public record CourseRowData(
            Long courseId, String courseTitle,
            Long teacherId, String teacherName,
            Long studentId, String studentName, String studentEmail) {
    }

    public static Course buildCourse(List<CourseRowData> rows) {
        CourseRowData first = rows.getFirst();

        Course course = new Course();
        course.setId(first.courseId());
        course.setTitle(first.courseTitle());

        if (first.teacherId() != null) {
            course.setTeacher(new Teacher(first.teacherId(), first.teacherName(), null));
        }

        Set<Student> students = rows.stream()
                .filter(r -> r.studentId() != null)
                .map(r -> new Student(r.studentId(), r.studentName(), r.studentEmail(), null))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        course.setStudents(students);
        return course;
    }

}
