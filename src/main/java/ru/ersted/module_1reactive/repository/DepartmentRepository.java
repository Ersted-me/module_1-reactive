package ru.ersted.module_1reactive.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.ersted.module_1reactive.entity.Department;

public interface DepartmentRepository extends ReactiveCrudRepository<Department, Long> {
}
