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
import java.util.Date;
import java.util.UUID;
import org.springframework.util.Assert;

public class Post {

    private UUID id;
    private String header;
    private String content;
    private String createdBy;

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "EEE, d MMM yyyy HH:mm:ss Z",
        timezone = "CET"
    )
    private Date createdOn;

    protected Post() {}

    public Post(
        UUID id,
        String header,
        String content,
        Date createdOn,
        String createdBy
    ) {
        this.id = id;
        this.header = header;
        this.content = content;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getHeader() {
        return header;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public static Builder newPost(String content) {
        return new Builder().withContent(content);
    }

    public static class Builder {

        private Post post = new Post();

        public Builder withContent(String content) {
            post.content = content;
            return this;
        }

        public Builder withHeader(String header) {
            post.header = header;
            return this;
        }

        public Builder createdBy(User user) {
            post.createdBy = user.getNickName();
            return this;
        }

        public Builder createdOn(Date date) {
            post.createdOn = date;
            return this;
        }

        public Post build() {
            if (post.id == null) {
                post.id = UUID.randomUUID();
            }

            Assert.notNull(post.header, "header may not be null");
            Assert.notNull(post.content, "content may not be null");
            Assert.notNull(post.createdBy, "created by may not be null");
            Assert.notNull(post.createdOn, "created on may not be null");

            return post;
        }
    }
}
