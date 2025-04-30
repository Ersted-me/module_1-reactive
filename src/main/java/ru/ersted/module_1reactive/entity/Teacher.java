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
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("Teacher")
public class Teacher implements Persistable<Long> {

    @Id
    private Long id;

    private String name;

    private Long departmentId;

    @Transient
    private Department department;

    @Transient
    private Set<Course> courses;

    public Teacher(Long id, String name, Long departmentId) {
        this.id = id;
        this.name = name;
        this.departmentId = departmentId;
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(id);
    }

}
