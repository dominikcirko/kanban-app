package com.company.kanban.repository;

import com.company.kanban.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface TaskRepository extends JpaRepository<Task, Long> {}
