package ru.ersted.module_1reactive.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ersted.module_1reactive.entity.Student;

public interface StudentRepository extends ReactiveCrudRepository<Student, Long> {
}
