package com.vvpanf.todolistbot.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    final String id;
    final Long userId;
    final List<TodoListDto> lists;
}
