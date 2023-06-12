package com.example.fulbot.repositories;

import com.example.fulbot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByChatId (Long chatId);
}
