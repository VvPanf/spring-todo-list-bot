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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "list")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TodoList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    User user;

    @Length(max = 255, message = "Name is too long")
    String name;

    @OneToMany(mappedBy = "list", fetch = FetchType.EAGER)
    @ToString.Exclude
    List<Item> items = new ArrayList<>();

    public TodoList(String name, User user) {
        this.name = name;
        this.user = user;
    }
}
