package com.example.fulbot.repositories;

import com.example.fulbot.entities.Calculation;
import com.example.fulbot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalculationRepository extends JpaRepository<Calculation, Long> {
    List<Calculation> findByUser (User user);

}
