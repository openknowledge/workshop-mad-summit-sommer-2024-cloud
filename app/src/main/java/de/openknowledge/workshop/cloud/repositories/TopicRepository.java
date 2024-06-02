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

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;

import static java.lang.String.format;

import de.openknowledge.workshop.cloud.models.Post;
import de.openknowledge.workshop.cloud.models.Topic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TopicRepository {
    @Value("${dynamodb.table}")
    private String tableName;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final Logger LOGGER = Logger.getLogger(
            TopicRepository.class.getSimpleName());

    private List<Topic> topics = new CopyOnWriteArrayList<>();

    public List<Topic> findAllTopicsOfCategory(UUID categoryId) {
        var queryTopicsExpressionValues = Map.of(
                ":pk", AttributeValue.builder().s(format("c->t:%s", categoryId)).build());

        var queryTopicsRequest = QueryRequest.builder().tableName(tableName)
                .keyConditionExpression("pk = :pk")
                .expressionAttributeValues(queryTopicsExpressionValues)
                .build();

        var topicItems = dynamoDbClient.query(queryTopicsRequest).items();

        var category = categoryRepository.findById(categoryId).orElseThrow();

        return topicItems.stream().map(topicItem -> {
            Topic topic = null;

            try {
                topic = Topic.newTopic(topicItem.get("title").s())
                        .withUUID(UUID.fromString(topicItem.get("sk").s().split(":")[1]))
                        .withDescription(topicItem.get("description").s())
                        .inCategory(category)
                        .createdBy(topicItem.get("created_by").s())
                        .createdOn(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z")
                                .parse(topicItem.get("created_on").s()))
                        .build();

                // TODO: This is not efficient
                var posts = this.findAllPostsOfTopic(topic.getId());

                posts.forEach(topic::addPost);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            return topic;
        }).toList();
    }

    public Optional<Topic> findTopicById(UUID id) {
        // TODO: Handle case where topic is not found

        // get category id

        var getCategoryIdKey = Map.of(
                "pk", AttributeValue.builder().s(format("t->c:%s", id)).build(),
                "sk", AttributeValue.builder().s("category_id").build());

        var getCategoryIdRequest = GetItemRequest.builder().tableName(tableName).key(getCategoryIdKey).build();

        var getCategoryIdItem = dynamoDbClient.getItem(getCategoryIdRequest).item();

        var categoryId = UUID.fromString(getCategoryIdItem.get("category_id").s());

        // get topic

        var getTopicKey = Map.of(
                "pk", AttributeValue.builder().s(format("c->t:%s", categoryId)).build(),
                "sk", AttributeValue.builder().s(format("t:%s", id)).build());

        var getTopicRequest = GetItemRequest.builder().tableName(tableName).key(getTopicKey).build();

        var getTopicItem = dynamoDbClient.getItem(getTopicRequest).item();

        var category = categoryRepository.findById(categoryId).orElseThrow();

        Topic topic = null;
        try {
            topic = Topic.newTopic(getTopicItem.get("title").s())
                    .withUUID(id)
                    .withDescription(getTopicItem.get("description").s())
                    .inCategory(category)
                    .createdOn(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z")
                            .parse(getTopicItem.get("created_on").s()))
                    .createdBy(getTopicItem.get("created_by").s())
                    .build();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // get all posts

        var queryPostsExpressionValues = Map.of(
                ":pk", AttributeValue.builder().s(format("t->p:%s", topic.getId())).build());

        var queryPostsRequest = QueryRequest.builder().tableName(tableName)
                .keyConditionExpression("pk = :pk")
                .expressionAttributeValues(queryPostsExpressionValues)
                .build();

        var postItems = dynamoDbClient.query(queryPostsRequest).items();

        var posts = postItems.stream().map(postItem -> {
            try {
                return new Post(
                        UUID.fromString(postItem.get("sk").s().split(":")[1]),
                        postItem.get("header").s(),
                        postItem.get("content").s(),
                        new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(postItem.get("created_on").s()),
                        postItem.get("created_by").s());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        posts.forEach(topic::addPost);

        return Optional.ofNullable(topic);
    }

    public void addTopic(Topic topic) {
        var topicCategoryIdItem = new HashMap<String, AttributeValue>();

        topicCategoryIdItem.put("pk", AttributeValue.builder().s(format("t->c:%s", topic.getId())).build());
        topicCategoryIdItem.put("sk", AttributeValue.builder().s("category_id").build());
        topicCategoryIdItem.put("category_id", AttributeValue.builder().s(topic.getCategoryId().toString()).build());

        var topicCategoryIdRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(topicCategoryIdItem)
                .build();

        dynamoDbClient.putItem(topicCategoryIdRequest);

        var topicItem = new HashMap<String, AttributeValue>();

        topicItem.put("pk", AttributeValue.builder().s(format("c->t:%s", topic.getCategoryId())).build());
        topicItem.put("sk", AttributeValue.builder().s(format("t:%s", topic.getId())).build());
        topicItem.put("title", AttributeValue.builder().s(topic.getTitle()).build());
        topicItem.put("description", AttributeValue.builder().s(topic.getDescription()).build());
        topicItem.put("created_by", AttributeValue.builder().s(topic.getCreatedBy()).build());
        topicItem.put("created_on", AttributeValue.builder()
                .s(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(topic.getCreatedOn())).build());

        var putTopicRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(topicItem)
                .build();

        dynamoDbClient.putItem(putTopicRequest);

        topic.getPosts().forEach(post -> {
            var postItem = new HashMap<String, AttributeValue>();

            postItem.put("pk", AttributeValue.builder().s(format("t->p:%s", topic.getId())).build());
            postItem.put("sk", AttributeValue.builder().s(format("p:%s", post.getId())).build());
            postItem.put("header", AttributeValue.builder().s(post.getHeader()).build());
            postItem.put("content", AttributeValue.builder().s(post.getContent()).build());
            postItem.put("created_by", AttributeValue.builder().s(post.getCreatedBy()).build());
            postItem.put("created_on", AttributeValue.builder().s(
                    new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(post.getCreatedOn())).build());

            var putPostRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(postItem)
                    .build();

            dynamoDbClient.putItem(putPostRequest);
        });
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
        throw new UnsupportedOperationException();
    }
}
