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

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.openknowledge.workshop.cloud.models.Topic;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TopicResult {

    private List<Topic> topics;
    private String categoryId;
    private String categoryTitle;

    public static TopicResult emptyResult() {
        return new TopicResult(Collections.<Topic>emptyList());
    }

    public TopicResult() {}

    public TopicResult(List<Topic> topics) {
        this.topics = Objects.requireNonNull(topics);
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return topics.isEmpty();
    }
}
