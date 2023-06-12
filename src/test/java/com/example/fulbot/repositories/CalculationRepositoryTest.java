package com.example.fulbot.repositories;

import com.example.fulbot.entities.Calculation;
import com.example.fulbot.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CalculationRepositoryTest {
    @Autowired
    private CalculationRepository calculationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSavingRoundTrip() {
        User user = entityManager.persist(new User("Galina", "Cherevatenko",
                "Galina",new Timestamp(System.currentTimeMillis()), "1234567890", 1L));
        Calculation calculation = new Calculation
                ( 10, 10, false, false,false, false,
                        false, false, false, 10, 610, user);

        Calculation result = calculationRepository.save(calculation);

        assertNotNull(result);
    }

    @Test
    void findByUser() {
        User user1 = entityManager.persist(new User("Galina", "Cherevatenko",
                "Galina",new Timestamp(System.currentTimeMillis()), "1234567890", 1L));
        User user2 = entityManager.persist(new User("Polina", "Cherevatenko",
                "Polina",new Timestamp(System.currentTimeMillis()), "1234567890", 2L));
        Calculation calculation1 = calculationRepository.save(new Calculation
                ( 10, 10, false, false,false, false,
                        false, false, false, 10, 610, user1));
        Calculation calculation2 = calculationRepository.save(new Calculation
                (20, 20, false, false,false, false,
                        false, false, false, 10, 810, user1));
        Calculation calculation3 = calculationRepository.save(new Calculation
                (30, 30, false, false,false, false,
                        false, false, false, 10, 1010, user2));

        List<Calculation> calculations = new ArrayList<>(Arrays.asList(calculation1, calculation2));

        List<Calculation> resultList = calculationRepository.findByUser(user1);

        assertNotNull(resultList);
        assertEquals(calculations.size(), resultList.size());
        assertEquals(calculations.get(0).getUser(), resultList.get(0).getUser());
    }
}