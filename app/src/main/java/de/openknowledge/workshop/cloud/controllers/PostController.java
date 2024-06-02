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
import de.openknowledge.workshop.cloud.models.Topic;
import de.openknowledge.workshop.cloud.repositories.CategoryRepository;
import de.openknowledge.workshop.cloud.repositories.TopicRepository;
import java.util.UUID;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

    private static final Logger LOGGER = Logger.getLogger(
        PostController.class.getSimpleName()
    );

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResult> getPosts(
        @RequestParam("category") String categoryId,
        @RequestParam("topic") String topicId
    ) {
        LOGGER.info(
            format(
                "RESTful call 'GET posts of topic %s in category %s'",
                topicId,
                categoryId
            )
        );

        PostResult postResult = PostResult.emptyResult();

        postResult.setPosts(
            topicRepository.findAllPostsOfTopic(UUID.fromString(topicId))
        );
        postResult.setTopicId(topicId);
        postResult.setTopicTitle(topicNameOf(UUID.fromString(topicId)));
        postResult.setCategoryId(categoryId);
        postResult.setCategoryTitle(
            categoryNameOf(UUID.fromString(categoryId))
        );

        return ResponseEntity.ok(postResult);
    }

    private String topicNameOf(UUID topicId) {
        return topicRepository
            .findTopicById(topicId)
            .map(Topic::getTitle)
            .orElse("unknown");
    }

    private String categoryNameOf(UUID categoryId) {
        return categoryRepository
            .findById(categoryId)
            .map(Category::getTitle)
            .orElse("unknown");
    }
}
