package com.example.fulbot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity(name= "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    private String  firstName;

    private String  lastName;

    private String  userName;

    private Timestamp registeredAt;

    @Size(min=10, max=12, message = "Телефон должен быть от 10 до 12 символов.")
    private String  phone;
    private long chatId;

    public User(String firstName, String lastName, String userName, Timestamp registeredAt, String phone, long chatId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.registeredAt = registeredAt;
        this.phone = phone;
        this.chatId = chatId;
    }
}

