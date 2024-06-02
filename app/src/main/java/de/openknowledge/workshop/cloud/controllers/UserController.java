/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.workshop.cloud.controllers;

import static java.lang.String.format;

import de.openknowledge.workshop.cloud.models.User;
import de.openknowledge.workshop.cloud.repositories.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(
        UserController.class.getSimpleName()
    );

    @Autowired
    private UserRepository repository;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getUsers(
        @RequestParam(value = "email", required = false) final String email
    ) {
        List<User> users;

        if (email == null) {
            LOGGER.info("RESTful call 'GET all users'");
            users = repository.findAll();
        } else {
            LOGGER.info(
                format("RESTful call 'GET user(s)' with email %s", email)
            );
            users = repository.findByEmail(email);
        }

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(users);
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        LOGGER.info(format("RESTful call 'GET user' with id %s", id));

        return ResponseEntity.of(repository.findById(UUID.fromString(id)));
    }
}
