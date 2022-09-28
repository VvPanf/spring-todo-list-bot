package com.vvpanf.todolistbot.service;

import com.vvpanf.todolistbot.dto.ItemDto;
import com.vvpanf.todolistbot.dto.TodoListDto;
import com.vvpanf.todolistbot.dto.UserDto;

import java.util.List;

public interface TodoListService {
    UserDto getUser(long userId);

    void addUser(long userId);

    List<TodoListDto> getLists(long userId);

    TodoListDto getList(long userId, String listId);

    TodoListDto getListByItemId(long userId, String itemId);

    ItemDto getItem(long userId, String itemId);

    void saveItem(long userId, String itemId, boolean checked);

    void addList(long userId, String title, List<String> itemContents);

    void removeList(long userId, String id);
}
