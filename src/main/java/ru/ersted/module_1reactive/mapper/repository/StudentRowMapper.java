package ru.ersted.module_1reactive.mapper.repository;

import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.entity.Student;
import ru.ersted.module_1reactive.entity.Teacher;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentRowMapper {

    public record StudentRowData(Long studentId, String studentName, String studentEmail,
                                 Long courseId, String courseTitle,
                                 Long teacherId, String teacherName) {

    }

    public static Student buildStudent(List<StudentRowData> rows) {

        StudentRowData first = rows.getFirst();

        Student st = new Student(first.studentId(), first.studentName(), first.studentEmail());
        Map<Long, Course> courses = new LinkedHashMap<>();

        for (StudentRowData row : rows) {
            if (row.courseId() == null) continue;

            Course c = courses.computeIfAbsent(row.courseId(), id -> {
                Course tmp = new Course();
                tmp.setId(id);
                tmp.setTitle(row.courseTitle());
                return tmp;
            });

            if (c.getTeacher() == null && row.teacherId() != null) {
                Teacher t = new Teacher(row.teacherId(), row.teacherName(), null);
                c.setTeacher(t);
            }
        }
        st.setCourses(new HashSet<>(courses.values()));
        return st;
    }


}
