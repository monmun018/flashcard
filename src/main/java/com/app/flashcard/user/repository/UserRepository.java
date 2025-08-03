package com.app.flashcard.user.repository;

import com.app.flashcard.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    public List<User> findByUserLoginID(String userLoginID);
}