package com.vvpanf.todolistbot.repo;

import com.vvpanf.todolistbot.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    User findByUserId(long userId);
    boolean existsByUserId(long userId);
}
