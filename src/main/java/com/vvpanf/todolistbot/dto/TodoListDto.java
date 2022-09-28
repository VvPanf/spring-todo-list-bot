package com.vvpanf.todolistbot.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TodoListDto {
    final String id;
    final String name;
    final List<ItemDto> items;
}
