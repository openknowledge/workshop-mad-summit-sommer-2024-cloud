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

import de.openknowledge.workshop.cloud.models.Category;
import de.openknowledge.workshop.cloud.models.Post;
import de.openknowledge.workshop.cloud.models.Topic;
import de.openknowledge.workshop.cloud.repositories.CategoryRepository;
import de.openknowledge.workshop.cloud.repositories.TopicRepository;
import de.openknowledge.workshop.cloud.repositories.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/topics")
public class TopicController {

    private static final Logger LOGGER = Logger.getLogger(
        TopicController.class.getSimpleName()
    );

    @Autowired
    private TopicRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TopicResult> getTopics(
        @RequestParam("category") String category
    ) {
        LOGGER.info(
            format("RESTful call 'GET topics of category %s'", category)
        );

        TopicResult topicResult = TopicResult.emptyResult();

        topicResult.setTopics(
            repository.findAllTopicsOfCategory(UUID.fromString(category))
        );
        topicResult.setCategoryId(category);
        topicResult.setCategoryTitle(categoryNameOf(UUID.fromString(category)));

        return ResponseEntity.ok(topicResult);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Topic> getTopic(@PathVariable("id") String id) {
        LOGGER.info(format("RESTful call 'GET topic' with id %s", id));

        return ResponseEntity.of(repository.findTopicById(UUID.fromString(id)));
    }

    @GetMapping(
        value = "{id}/posts",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Post>> getPosts(@PathVariable("id") String id) {
        LOGGER.info(format("RESTful call 'GET all posts of topic %s'", id));

        List<Post> posts = repository.findAllPostsOfTopic(UUID.fromString(id));

        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(posts);
        }
    }

    @GetMapping(
        value = "{did}/posts/{pid}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Post> getPost(
        @PathVariable("did") String topicId,
        @PathVariable("pid") String postId
    ) {
        LOGGER.info(
            format("RESTful call 'GET post %s of topic %s'", postId, topicId)
        );

        Optional<Post> post = repository.findPostById(
            UUID.fromString(topicId),
            UUID.fromString(postId)
        );

        return ResponseEntity.of(post);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addTopic(
        @RequestParam("category") String categoryId,
        @RequestBody NewTopic newTopic
    ) {
        LOGGER.info(
            format(
                "RESTful call 'ADD topic %s in category %s'",
                newTopic,
                categoryId
            )
        );

        var topic =
            Topic.newTopic(newTopic.getTitle()).withDescription(newTopic.getDescription()).withUUID(UUID.randomUUID()).createdOn(new Date())
                .createdBy((userRepository.findAll().stream().findFirst().orElseThrow().getNickName()))
                .inCategory(categoryRepository.findById(UUID.fromString(categoryId)).orElseThrow()).build();

        repository.addTopic(topic);

        return ResponseEntity.ok(null);
    }

    private String categoryNameOf(UUID categoryId) {
        return categoryRepository
            .findById(categoryId)
            .map(Category::getTitle)
            .orElse("unknown");
    }
}
