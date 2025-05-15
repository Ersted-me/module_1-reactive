package ru.ersted.module_1reactive.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import ru.ersted.module_1reactive.dto.generated.StudentUpdateRq;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("Student")
public class Student implements Persistable<Long> {

    @Id
    private Long id;

    private String name;

    private String email;

    @Transient
    private Set<Course> courses = new HashSet<>();

    public Student(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(id);
    }

}
