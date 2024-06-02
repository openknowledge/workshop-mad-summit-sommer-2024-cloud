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

public class Category {

    private UUID id;
    private String title;
    private String description;
    private Integer topicsCount;
    private Integer postsCount;
    private String slug;

    protected Category() {}

    public Category(UUID id, String title, String description, String slug) {
        this.id = id;
        this.title = title;
        this.slug = slug;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getTopicsCount() {
        return topicsCount;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public String getSlug() {
        return slug;
    }

    public static Builder newCategory(String title) {
        return new Builder().withTitle(title);
    }

    public static class Builder {

        private Category category = new Category();

        public Builder withUUID(UUID id) {
            category.id = id;
            return this;
        }

        public Builder withTitle(String title) {
            category.title = title;
            return this;
        }

        public Builder withDescription(String description) {
            category.description = description;
            return this;
        }

        public Builder withTopicsCount(Integer topicsCount) {
            category.topicsCount = topicsCount;
            return this;
        }

        public Builder withPostsCount(Integer postsCount) {
            category.postsCount = postsCount;
            return this;
        }

        public Builder withSlug(String slug) {
            category.slug = slug;
            return this;
        }

        public Category build() {
            if (category.id == null) {
                category.id = UUID.randomUUID();
            }

            Assert.notNull(category.title, "title may not be null");
            Assert.notNull(category.description, "description may not be null");
            Assert.notNull(category.slug, "slug may not be null");

            // TODO: check topics and posts counts ration for plausibility

            return category;
        }
    }
}
