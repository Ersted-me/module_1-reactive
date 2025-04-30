package ru.ersted.module_1reactive.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("students_courses")
public record StudentsCourses(
        @Column("student_id") Long studentId,
        @Column("course_id") Long courseId) {
}
