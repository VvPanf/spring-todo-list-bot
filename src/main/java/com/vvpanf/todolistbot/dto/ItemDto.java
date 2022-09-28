package com.vvpanf.todolistbot.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    final String id;
    final String content;
    final boolean checked;
}
