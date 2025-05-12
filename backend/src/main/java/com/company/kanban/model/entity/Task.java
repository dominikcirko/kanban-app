package com.company.kanban.model.entity;

import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Task {

    @Id
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Priority priority;

}
