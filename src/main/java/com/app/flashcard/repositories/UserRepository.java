package com.app.flashcard.repositories;

import com.app.flashcard.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    public List<User> findByUserLoginID(String userLoginID);
}
