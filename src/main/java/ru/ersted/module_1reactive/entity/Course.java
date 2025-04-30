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
@AllArgsConstructor
@NoArgsConstructor
@Table("Course")
public class Course implements Persistable<Long> {

    @Id
    private Long id;

    private String title;

    private Long teacherId;

    @Transient
    private Teacher teacher;

    @Transient
    private Set<Student> students;

    public Course(Long id, String title, Long teacherId) {
        this.id = id;
        this.title = title;
        this.teacherId = teacherId;
    }

    @Override
    public boolean isNew() {
        return Objects.isNull(id);
    }

}
