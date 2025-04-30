package ru.ersted.module_1reactive.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ersted.module_1reactive.entity.Teacher;

public interface TeacherRepository extends ReactiveCrudRepository<Teacher, Long> {
}
