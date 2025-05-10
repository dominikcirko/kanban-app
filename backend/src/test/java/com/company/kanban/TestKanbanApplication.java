package com.company.kanban;

import org.springframework.boot.SpringApplication;

public class TestKanbanApplication {

	public static void main(String[] args) {
		SpringApplication.from(KanbanApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
