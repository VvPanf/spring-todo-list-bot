package com.vvpanf.todolistbot.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Table(name = "list_item")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "list_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    TodoList list;

    @Length(max = 255, message = "Content is too long")
    String content;

    boolean checked;

    public Item(String content, boolean checked, TodoList list) {
        this.content = content;
        this.checked = checked;
        this.list = list;
    }
}
