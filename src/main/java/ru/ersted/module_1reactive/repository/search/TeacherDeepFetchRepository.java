package ru.ersted.module_1reactive.repository.search;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.ersted.module_1reactive.entity.Teacher;
import ru.ersted.module_1reactive.mapper.repository.TeacherRowMapper;

@Repository
@RequiredArgsConstructor
public class TeacherDeepFetchRepository {

    private final DatabaseClient databaseClient;


    public Flux<Teacher> findAllWithDepartmentAndCourses() {

        String sql = """
                SELECT
                    t.id   AS t_id,  t.name AS t_name,
                    d.id   AS d_id,  d.name AS d_name,
                    c.id   AS c_id,  c.title AS c_title
                FROM teacher t
                LEFT JOIN department d ON d.id = t.department_id
                LEFT JOIN course     c ON c.teacher_id = t.id
                ORDER BY t.id
                """;

        return databaseClient.sql(sql)
                .map((row, m) -> new TeacherRowMapper.TeacherRowData(
                        row.get("t_id", Long.class),
                        row.get("t_name", String.class),
                        row.get("d_id", Long.class),
                        row.get("d_name", String.class),
                        row.get("c_id", Long.class),
                        row.get("c_title", String.class)))
                .all()
                .groupBy(TeacherRowMapper.TeacherRowData::teacherId)
                .flatMap(g -> g.collectList()
                        .map(TeacherRowMapper::buildTeacher));
    }

}
