package com.vvpanf.todolistbot.service.impl;

import com.vvpanf.todolistbot.dto.ItemDto;
import com.vvpanf.todolistbot.dto.TodoListDto;
import com.vvpanf.todolistbot.dto.UserDto;
import com.vvpanf.todolistbot.entity.Item;
import com.vvpanf.todolistbot.entity.TodoList;
import com.vvpanf.todolistbot.entity.User;
import com.vvpanf.todolistbot.repo.UserRepo;
import com.vvpanf.todolistbot.service.TodoListService;
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
public class TodoListMongoServiceImpl implements TodoListService {
    final UserRepo userRepo;

    @Transactional
    @Override
    public UserDto getUser(long userId) {
        addUser(userId);
        User user = userRepo.findByUserId(userId);
        List<TodoListDto> todoListDtos = user.getLists().stream().map(list -> {
            List<ItemDto> itemsDtos = list.getItems().stream().map(item -> new ItemDto(item.getId(), item.getContent(), item.isChecked())).collect(Collectors.toList());
            return new TodoListDto(list.getId(), list.getName(), itemsDtos);
        }).collect(Collectors.toList());
        return new UserDto(user.getId(), user.getUserId(), todoListDtos);
    }

    @Transactional
    @Override
    public void addUser(long userId) {
        if (!userRepo.existsByUserId(userId)) {
            userRepo.save(new User(userId));
        }
    }

    @Override
    public List<TodoListDto> getLists(long userId) {
        return getUser(userId).getLists();
    }

    @Override
    public TodoListDto getList(long userId, String listId) {
        User user = userRepo.findByUserId(userId);
        TodoList todoList = user.getLists().stream().filter(list -> list.getId().equals(listId)).findFirst().orElseThrow();
        List<ItemDto> itemDtos = todoList.getItems().stream().map(item -> new ItemDto(item.getId(), item.getContent(), item.isChecked())).collect(Collectors.toList());
        return new TodoListDto(todoList.getId(), todoList.getName(), itemDtos);
    }

    @Override
    public TodoListDto getListByItemId(long userId, String itemId) {
        User user = userRepo.findByUserId(userId);
        TodoList todoList = findListByItemId(itemId, user.getLists());
        if (todoList == null) {
            return null;
        }
        List<ItemDto> itemDtos = todoList.getItems().stream().map(item -> new ItemDto(item.getId(), item.getContent(), item.isChecked())).collect(Collectors.toList());
        return new TodoListDto(todoList.getId(), todoList.getName(), itemDtos);
    }

    @Override
    public ItemDto getItem(long userId, String itemId) {
        User user = userRepo.findByUserId(userId);
        List<TodoList> todoLists = user.getLists();
        return todoLists.stream().flatMap(todoList -> todoList.getItems().stream()).filter(item -> itemId.equals(item.getId()))
                .findFirst().map(item -> new ItemDto(item.getId(), item.getContent(), item.isChecked())).orElse(null);
    }

    @Transactional
    @Override
    public void saveItem(long userId, String itemId, boolean checked) {
        User user = userRepo.findByUserId(userId);
        TodoList todoList = findListByItemId(itemId, user.getLists());
        if (todoList == null) {
            return;
        }
        Item item = todoList.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow();
        item.setChecked(checked);
        userRepo.save(user);
    }

    @Transactional
    @Override
    public void addList(long userId, String title, List<String> itemContents) {
        User user = userRepo.findByUserId(userId);
        List<Item> items = itemContents.stream().map(content -> new Item(content, false)).collect(Collectors.toList());
        TodoList todoList = new TodoList(title, items);
        user.getLists().add(todoList);
        userRepo.save(user);
    }

    @Transactional
    public void removeList(long userId, String id) throws NoSuchElementException {
        User user = userRepo.findByUserId(userId);
        user.setLists(user.getLists().stream().filter(item -> !item.getId().equals(id)).collect(Collectors.toList()));
        userRepo.save(user);
    }

    private TodoList findListByItemId(String itemId, List<TodoList> todoLists) {
        for (TodoList todoList : todoLists) {
            for (Item item : todoList.getItems()) {
                if (itemId.equals(item.getId())) {
                    return todoList;
                }
            }
        }
        return null;
    }
}
