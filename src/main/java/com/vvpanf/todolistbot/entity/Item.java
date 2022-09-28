package com.vvpanf.todolistbot.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    String id;
    String content;
    boolean checked;

    public Item(String content, boolean checked) {
        this.id = new ObjectId().toString();
        this.content = content;
        this.checked = checked;
    }
}
