package ru.ersted.module_1reactive.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("Department")
public class Department implements Persistable<Long> {

    @Id
    private Long id;

    private String name;

    private Long headOfDepartmentId;

    @Transient
    private Teacher headOfDepartment;

    public Department(Long id, String name, Long headOfDepartmentId) {
        this.id = id;
        this.name = name;
        this.headOfDepartmentId = headOfDepartmentId;
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(id);
    }

}
