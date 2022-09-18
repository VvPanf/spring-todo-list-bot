package com.vvpanf.todolistbot.repo;

import com.vvpanf.todolistbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUserId(long userId);
    boolean existsByUserId(long userId);
}
