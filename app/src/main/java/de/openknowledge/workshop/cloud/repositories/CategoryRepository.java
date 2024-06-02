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

import static java.util.Collections.unmodifiableList;

import de.openknowledge.workshop.cloud.models.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component
public class CategoryRepository {

    private static final Logger LOGGER = Logger.getLogger(
        CategoryRepository.class.getSimpleName()
    );

    private List<Category> categories = new CopyOnWriteArrayList<>();

    public List<Category> findAll() {
        return unmodifiableList(categories);
    }

    public Optional<Category> findById(UUID id) {
        return categories
            .stream()
            .filter(topic -> topic.getId().equals(id))
            .findAny();
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public Integer countCategories() {
        return categories != null ? categories.size() : 0;
    }
}
