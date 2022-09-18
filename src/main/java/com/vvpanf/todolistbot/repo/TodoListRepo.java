package com.vvpanf.todolistbot.repo;

import com.vvpanf.todolistbot.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoListRepo extends JpaRepository<TodoList, Long> {
}
