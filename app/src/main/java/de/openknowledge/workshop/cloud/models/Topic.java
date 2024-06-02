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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.util.Assert;

public class Topic {

    private UUID id;
    private String title;
    private String description;

    @JsonIgnore
    private Category parentCategory;

    private String createdBy;

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "EEE, d MMM yyyy HH:mm:ss Z",
        timezone = "CET"
    )
    private Date createdOn;

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "EEE, d MMM yyyy HH:mm:ss Z",
        timezone = "CET"
    )
    private Date lastPost;

    @JsonIgnore
    private List<Post> posts;

    protected Topic() {
        // for frameworks
    }

    public Topic(
        UUID id,
        String title,
        Category parentCategory,
        String createdBy,
        Date createdOn
    ) {
        this.id = id;
        this.title = title;
        this.parentCategory = parentCategory;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
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

    public Integer getUserCount() {
        return posts.stream().map(Post::getCreatedBy).collect(Collectors.toSet()).size();
    }

    public Integer getPostCount() {
        return posts.size();
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getLastPost() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @JsonIgnore
    public Category getParentCategory() {
        return parentCategory;
    }

    public String getCategoryTitle() {
        return parentCategory.getTitle();
    }

    public UUID getCategoryId() {
        return parentCategory.getId();
    }

    public List<Post> getPosts() {
        return posts
            .stream()
            .sorted((p1, p2) -> p1.getCreatedOn().compareTo(p2.getCreatedOn()))
            .collect(Collectors.toList());
    }

    public void addPost(Post newPost) {
        this.posts.add(newPost);
    }

    public static Builder newTopic(String title) {
        return new Builder().withTitle(title);
    }

    public static class Builder {

        private Topic topic = new Topic();

        public Builder withUUID(UUID id) {
            topic.id = id;
            return this;
        }

        public Builder inCategory(Category category) {
            topic.parentCategory = category;
            return this;
        }

        public Builder withDescription(String description) {
            topic.description = description;
            return this;
        }

        public Builder withTitle(String title) {
            topic.title = title;
            return this;
        }

        public Builder createdBy(String nickname) {
            topic.createdBy = nickname;
            return this;
        }

        public Builder createdOn(Date date) {
            topic.createdOn = date;
            return this;
        }

        public Builder lastPost(Date date) {
            topic.lastPost = date;
            return this;
        }

        public Topic build() {
            if (topic.id == null) {
                topic.id = UUID.randomUUID();
            }

            Assert.notNull(topic.title, "title may not be null");
            Assert.notNull(
                topic.parentCategory,
                "parent category may not be null"
            );

            Assert.notNull(topic.createdBy, "created by may not be null");
            Assert.notNull(topic.createdOn, "created on may not be null");

            topic.posts = new ArrayList<>();

            return topic;
        }
    }
}
