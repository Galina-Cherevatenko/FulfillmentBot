package com.example.fulbot.services;

import com.example.fulbot.entities.Calculation;
import com.example.fulbot.entities.User;
import com.example.fulbot.exceptions.UserNotFoundException;
import com.example.fulbot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URISyntaxException;

@Service
public class BitrixService {
    @Autowired
    private UserRepository userRepository;
    private RestTemplate restTemplate = new RestTemplate();
    String BTRX = "";

    public void postLead (long chatId, Calculation calculation) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        User user = userRepository.findByChatId(chatId).orElseThrow(UserNotFoundException::new);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(BTRX)
                .queryParam("FIELDS[TITLE]", "лид из java")
                .queryParam("FIELDS[NAME]", user.getFirstName())
                .queryParam("FIELDS[LAST_NAME]", user.getLastName())
                .queryParam("FIELDS[PHONE][0][VALUE]", user.getPhone())
                .queryParam("FIELDS[COMMENTS]", calculation.toString())
                .build()
                .toUriString();

        System.out.println(urlTemplate);

        ResponseEntity<String> response = restTemplate.postForEntity(urlTemplate, requestEntity, String.class);

    }

}