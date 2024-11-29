package com.ioob.backend.domain.kanban.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "task_files")
public class TaskFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
}
