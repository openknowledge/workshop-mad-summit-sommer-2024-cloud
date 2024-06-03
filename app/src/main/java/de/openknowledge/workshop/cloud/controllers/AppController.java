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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/")
public class AppController {
    private static final String RANDOM_UUID = UUID.randomUUID().toString();

    private static final Logger LOGGER = Logger.getLogger(
        AppController.class.getSimpleName()
    );

    @GetMapping(value = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getId() {
        LOGGER.info("RESTful call 'GET id'");

        return RANDOM_UUID;
    }
}
