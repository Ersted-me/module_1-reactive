package ru.ersted.module_1reactive.repository.search;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.entity.Department;
import ru.ersted.module_1reactive.entity.Teacher;
import ru.ersted.module_1reactive.mapper.repository.TeacherRowMapper;

@Repository
@RequiredArgsConstructor
public class DepartmentDeepFetchRepository {

    private final DatabaseClient databaseClient;


    public Mono<Department> findByDepartmentId(Long departmentId) {

        String sql = """
                SELECT 
                    d.id as d_id,
                    d.name as d_name,
                    t.id as t_id,
                    t.name as t_name
                FROM department d
                LEFT JOIN public.teacher t on d.head_of_department_id = t.id
                WHERE d.id = :department_id
                """;

        return databaseClient.sql(sql)
                .bind("department_id", departmentId)
                .map((row, __) -> {
                    Department department = new Department();
                    department.setId(row.get("d_id", Long.class));
                    department.setName(row.get("d_name", String.class));

                    Long tId = row.get("t_id", Long.class);
                    if (tId != null) {
                        Teacher teacher = new Teacher();
                        teacher.setId(tId);
                        teacher.setName(row.get("t_name", String.class));
                        department.setHeadOfDepartmentId(teacher.getId());
                        department.setHeadOfDepartment(teacher);
                    }
                    return department;
                })
                .all()
                .singleOrEmpty();
    }

}
