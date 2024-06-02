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

import de.openknowledge.workshop.cloud.models.Post;
import de.openknowledge.workshop.cloud.models.Topic;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TopicRepository {

    private static final Logger LOGGER = Logger.getLogger(
        TopicRepository.class.getSimpleName()
    );

    private List<Topic> topics = new CopyOnWriteArrayList<>();

    public List<Topic> findAllTopicsOfCategory(UUID categoryId) {
        return topics
            .stream()
            .filter(topic ->
                topic.getParentCategory().getId().equals(categoryId)
            )
            .collect(Collectors.toList());
    }

    public Optional<Topic> findTopicById(UUID id) {
        return topics
            .stream()
            .filter(topic -> topic.getId().equals(id))
            .findAny();
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public Integer countTopics() {
        return topics != null ? topics.size() : 0;
    }

    public List<Post> findAllPostsOfTopic(UUID topicId) {
        Optional<Topic> topic = findTopicById(topicId);
        if (topic.isPresent()) {
            return topic.get().getPosts();
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<Post> findPostById(UUID topicId, UUID postId) {
        Optional<Topic> topic = findTopicById(topicId);

        if (topic.isPresent()) {
            return topic
                .get()
                .getPosts()
                .stream()
                .filter(post -> post.getId().equals(postId))
                .findAny();
        }

        return Optional.empty();
    }
}
