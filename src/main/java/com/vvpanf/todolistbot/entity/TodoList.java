package com.vvpanf.todolistbot.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TodoList {
    @Id
    String id;
    String name;
    List<Item> items;

    public TodoList(String name, List<Item> items) {
        this.id = new ObjectId().toString();
        this.name = name;
        this.items = items;
    }
}
