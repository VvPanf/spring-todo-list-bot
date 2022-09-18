package com.vvpanf.todolistbot.service;

import com.vvpanf.todolistbot.entity.Item;
import com.vvpanf.todolistbot.entity.TodoList;
import com.vvpanf.todolistbot.entity.User;
import com.vvpanf.todolistbot.repo.ItemRepo;
import com.vvpanf.todolistbot.repo.TodoListRepo;
import com.vvpanf.todolistbot.repo.UserRepo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TodoListService {
    final UserRepo userRepo;
    final TodoListRepo todoListRepo;
    final ItemRepo itemRepo;

    public User getUser(long userId) {
        return userRepo.findByUserId(userId);
    }

    @Transactional
    public void addUser(long userId) {
        if (!userRepo.existsByUserId(userId)) {
            userRepo.save(new User(userId));
        }
    }

    public List<TodoList> getLists(long userId) {
        return getUser(userId).getLists();
    }

    public TodoList getList(long listId) { return todoListRepo.getById(listId); }

    public Item getItem(long itemId) {
        return itemRepo.getById(itemId);
    }

    public void saveItem(Item item) {
        itemRepo.save(item);
    }

    @Transactional
    public void addList(long userId, String title, List<String> itemContents) {
        User user = getUser(userId);
        TodoList todoList = new TodoList(title, user);
        List<Item> items = itemContents.stream().map(content -> new Item(content, false, todoList)).collect(Collectors.toList());
        todoListRepo.save(todoList);
        itemRepo.saveAll(items);
    }

    @Transactional
    public void removeList(long userId, long listId) throws NoSuchElementException {
        User user = getUser(userId);
        TodoList todoList = user.getLists().stream().filter(item -> item.getId().equals(listId)).findFirst().orElseThrow();
        todoListRepo.delete(todoList);
    }
}
