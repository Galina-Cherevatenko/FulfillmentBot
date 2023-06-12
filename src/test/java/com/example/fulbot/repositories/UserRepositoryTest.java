package com.example.fulbot.repositories;

import com.example.fulbot.entities.User;
import com.example.fulbot.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSavingRoundTrip() {
        User user = new User("Galina", "Cherevatenko",
                "Galina",new Timestamp(System.currentTimeMillis()), "1234567890", 1L);
        User result = userRepository.save(user);

        assertNotNull(result);
    }
    @Test
    void findByChatId() {
        User user1 = userRepository.save(new User("Galina", "Cherevatenko",
                "Galina",new Timestamp(System.currentTimeMillis()), "1234567890", 1L));
        User user2 = userRepository.save(new User("Polina", "Cherevatenko",
                "Polina",new Timestamp(System.currentTimeMillis()), "1234567890", 2L));
        User user3 = userRepository.save(new User("Malina", "Cherevatenko",
                "Malina",new Timestamp(System.currentTimeMillis()), "1234567890", 3L));

        User result = userRepository.findByChatId(2L).orElseThrow(UserNotFoundException::new);

        assertNotNull(result);
        assertEquals(user2.getFirstName(),result.getFirstName());
    }
}