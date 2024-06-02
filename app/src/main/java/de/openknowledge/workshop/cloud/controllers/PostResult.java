package de.openknowledge.workshop.cloud.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.openknowledge.workshop.cloud.models.Post;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PostResult {

    private List<Post> posts;
    private String topicId;
    private String topicTitle;
    private String categoryId;
    private String categoryTitle;

    public static PostResult emptyResult() {
        return new PostResult(Collections.<Post>emptyList());
    }

    public PostResult() {}

    public PostResult(List<Post> posts) {
        this.posts = Objects.requireNonNull(posts);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
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

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return posts.isEmpty();
    }
}
