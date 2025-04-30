package ru.ersted.module_1reactive.repository.search;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.entity.Student;
import ru.ersted.module_1reactive.mapper.repository.StudentRowMapper;

@Repository
@RequiredArgsConstructor
public class StudentDeepFetchRepository {

    private final DatabaseClient databaseClient;


    public Flux<Student> findAll() {
        String sql = """
                    SELECT
                        s.id    AS s_id,
                        s.name  AS s_name,
                        s.email AS s_email,
                
                        c.id    AS c_id,
                        c.title AS c_title,
                
                        t.id    AS t_id,
                        t.name  AS t_name
                    FROM student            s
                    LEFT JOIN students_courses sc ON sc.student_id = s.id
                    LEFT JOIN course             c ON c.id         = sc.course_id
                    LEFT JOIN teacher            t ON t.id         = c.teacher_id
                    ORDER BY s.id
                """;

        return databaseClient.sql(sql)
                .map((row, m) -> new StudentRowMapper.StudentRowData(
                        row.get("s_id", Long.class),
                        row.get("s_name", String.class),
                        row.get("s_email", String.class),
                        row.get("c_id", Long.class),
                        row.get("c_title", String.class),
                        row.get("t_id", Long.class),
                        row.get("t_name", String.class)
                ))
                .all()
                .groupBy(StudentRowMapper.StudentRowData::studentId)
                .flatMap(grp -> grp.collectList().map(StudentRowMapper::buildStudent));
    }

    public Mono<Student> findById(Long studentId) {
        String sql = """
                    SELECT
                        s.id    AS s_id,
                        s.name  AS s_name,
                        s.email AS s_email,
                
                        c.id    AS c_id,
                        c.title AS c_title,
                
                        t.id    AS t_id,
                        t.name  AS t_name
                    FROM student            s
                    LEFT JOIN students_courses sc ON sc.student_id = s.id
                    LEFT JOIN course             c ON c.id         = sc.course_id
                    LEFT JOIN teacher            t ON t.id         = c.teacher_id
                    WHERE s.id = :student_id
                    ORDER BY s.id
                """;

        return databaseClient.sql(sql)
                .bind("student_id", studentId)
                .map((row, m) -> new StudentRowMapper.StudentRowData(
                        row.get("s_id", Long.class),
                        row.get("s_name", String.class),
                        row.get("s_email", String.class),
                        row.get("c_id", Long.class),
                        row.get("c_title", String.class),
                        row.get("t_id", Long.class),
                        row.get("t_name", String.class)
                ))
                .all()
                .groupBy(StudentRowMapper.StudentRowData::studentId)
                .flatMap(grp -> grp.collectList().map(StudentRowMapper::buildStudent))
                .singleOrEmpty();
    }
}
