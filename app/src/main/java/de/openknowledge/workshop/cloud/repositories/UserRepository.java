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
package de.openknowledge.workshop.cloud.repositories;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import de.openknowledge.workshop.cloud.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(
        UserRepository.class.getSimpleName()
    );

    private List<User> users = new CopyOnWriteArrayList<>();

    /**
     * Returns list of users
     *
     * @return list of users
     */
    public List<User> findAll() {
        return unmodifiableList(users);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public Integer countUsers() {
        return users != null ? users.size() : 0;
    }

    public Optional<User> findById(UUID id) {
        return users.stream().filter(user -> user.getId().equals(id)).findAny();
    }

    public List<User> findByEmail(String email) {
        return users
            .stream()
            .filter(user -> user.getEmail().equals(email))
            .collect(Collectors.toList());
    }
}
