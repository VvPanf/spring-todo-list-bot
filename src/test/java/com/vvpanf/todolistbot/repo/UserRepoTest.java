package com.vvpanf.todolistbot.repo;

import com.vvpanf.todolistbot.entity.Item;
import com.vvpanf.todolistbot.entity.TodoList;
import com.vvpanf.todolistbot.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepoTest {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TodoListRepo todoListRepo;
    @Autowired
    private ItemRepo itemRepo;

    @BeforeEach
    public void init() {
        User user = new User(12345);
        TodoList list = new TodoList("TodoList 1", user);
        List<Item> items = List.of(
                new Item("Content 1", true, list),
                new Item("Content 2", false, list)
        );
        userRepo.save(user);
        todoListRepo.save(list);
        itemRepo.saveAll(items);
    }

    @Test
//    @Rollback(false)
    public void testSaveNewUser() {
        User search = userRepo.findByUserId(12345);
        assertThat(search.getUserId()).isEqualTo(12345);
        assertThat(search.getLists().size()).isEqualTo(1);
        assertThat(search.getLists().get(0).getName()).isEqualTo("TodoList 1");
        assertThat(search.getLists().get(0).getItems().size()).isEqualTo(2);
    }
}