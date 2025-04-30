package ru.ersted.module_1reactive.repository.search;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.entity.Course;
import ru.ersted.module_1reactive.entity.Teacher;
import ru.ersted.module_1reactive.mapper.repository.CourseRowMapper;

@Repository
@RequiredArgsConstructor
public class CourseDeepFetchRepository {

    private final DatabaseClient databaseClient;

    public Flux<Course> findByStudentId(Long studentId) {
        String sql = """
                    SELECT
                        c.id    AS c_id,
                        c.title AS c_title,
                        t.id    AS t_id,
                        t.name  AS t_name
                    FROM students_courses sc
                    JOIN course   c ON c.id = sc.course_id
                    LEFT JOIN teacher t ON t.id = c.teacher_id
                    WHERE sc.student_id = :student_id
                    ORDER BY c.id
                """;

        return databaseClient.sql(sql)
                .bind("student_id", studentId)
                .map((row, __) -> {
                    Course course = new Course();
                    course.setId(row.get("c_id", Long.class));
                    course.setTitle(row.get("c_title", String.class));

                    Long tid = row.get("t_id", Long.class);
                    if (tid != null) {
                        Teacher teacher = new Teacher();
                        teacher.setId(tid);
                        teacher.setName(row.get("t_name", String.class));
                        course.setTeacher(teacher);
                    }
                    return course;
                })
                .all();
    }

    public Flux<Course> findAll() {
        String sql = """
                SELECT
                    c.id   AS c_id,   c.title AS c_title,
                    t.id   AS t_id,   t.name  AS t_name,
                    s.id   AS s_id,   s.name  AS s_name, s.email AS s_email
                FROM   course               c
                LEFT   JOIN teacher         t  ON t.id = c.teacher_id
                LEFT   JOIN students_courses sc ON sc.course_id = c.id
                LEFT   JOIN student          s  ON s.id = sc.student_id
                ORDER  BY c.id
                """;

        return databaseClient.sql(sql)
                .map((row, m) -> new CourseRowMapper.CourseRowData(
                        row.get("c_id", Long.class),
                        row.get("c_title", String.class),
                        row.get("t_id", Long.class),
                        row.get("t_name", String.class),
                        row.get("s_id", Long.class),
                        row.get("s_name", String.class),
                        row.get("s_email", String.class)))
                .all()
                .groupBy(CourseRowMapper.CourseRowData::courseId)
                .flatMap(g -> g.collectList()
                        .map(CourseRowMapper::buildCourse));
    }

    public Mono<Course> findByCourseId(Long courseId) {
        String sql = """
                SELECT
                    c.id   AS c_id,   c.title AS c_title,
                    t.id   AS t_id,   t.name  AS t_name,
                    s.id   AS s_id,   s.name  AS s_name, s.email AS s_email
                FROM   course               c
                LEFT   JOIN teacher         t  ON t.id = c.teacher_id
                LEFT   JOIN students_courses sc ON sc.course_id = c.id
                LEFT   JOIN student          s  ON s.id = sc.student_id
                WHERE sc.course_id = :course_id
                ORDER  BY c.id
                """;

        return databaseClient.sql(sql)
                .bind("course_id", courseId)
                .map((row, m) -> new CourseRowMapper.CourseRowData(
                        row.get("c_id", Long.class),
                        row.get("c_title", String.class),
                        row.get("t_id", Long.class),
                        row.get("t_name", String.class),
                        row.get("s_id", Long.class),
                        row.get("s_name", String.class),
                        row.get("s_email", String.class)))
                .all()
                .groupBy(CourseRowMapper.CourseRowData::courseId)
                .flatMap(g -> g.collectList()
                        .map(CourseRowMapper::buildCourse))
                .singleOrEmpty();
    }

}
