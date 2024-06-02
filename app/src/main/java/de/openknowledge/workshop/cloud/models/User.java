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
package de.openknowledge.workshop.cloud.models;

import java.util.UUID;
import org.springframework.util.Assert;

public class User {

    private UUID id;
    private String firstName;
    private String lastName;
    private String nickName;
    private String email;

    protected User() {}

    public User(
        UUID id,
        String firstName,
        String lastName,
        String nickName,
        String email
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public static Builder newUser(String firstName, String lastName) {
        return new Builder().withName(firstName, lastName);
    }

    public static class Builder {

        private User user = new User();

        public Builder withUUID(UUID id) {
            user.id = id;
            return this;
        }

        public Builder withName(String firstName, String lastName) {
            user.firstName = firstName;
            user.lastName = lastName;
            return this;
        }

        public Builder withNickName(String nickName) {
            user.nickName = nickName;
            return this;
        }

        public Builder withEmail(String email) {
            user.email = email;
            return this;
        }

        public User build() {
            if (user.id == null) {
                user.id = UUID.randomUUID();
            }

            Assert.notNull(user.firstName, "first name may not be null");
            Assert.notNull(user.lastName, "last name may not be null");
            Assert.notNull(user.email, "email may not be null");

            if (user.nickName == null) {
                user.nickName = user.firstName + "." + user.lastName;
            }
            return user;
        }
    }
}
