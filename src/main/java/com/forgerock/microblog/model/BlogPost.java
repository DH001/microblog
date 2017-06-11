package com.forgerock.microblog.model;

import java.time.Instant;

/**
 * A single post on the blog at a specific point in time with textual content and an optional user id.
 */
public class BlogPost {

    private String id;

    private Instant timestamp;

    private String body;

    private String userId;

    public BlogPost(final Instant timestamp, final String body) {

        this.timestamp = timestamp;
        this.body = body;
    }

    public BlogPost(final Instant timestamp, final String body, final String userId) {

        this.timestamp = timestamp;
        this.body = body;
        this.userId = userId;
    }

    public String getId() {

        return id;
    }

    public Instant getTimestamp() {

        return timestamp;
    }

    public String getBody() {

        return body;
    }

    public String getUserId() {

        return userId;
    }

    public static final class BlogPostBuilder {

        private String id;
        private Instant timestamp;
        private String body;
        private String userId;

        private BlogPostBuilder() {}

        public static BlogPostBuilder aBlogPost() { return new BlogPostBuilder();}

        public BlogPostBuilder withId(String id) {

            this.id = id;
            return this;
        }

        public BlogPostBuilder withTimestamp(Instant timestamp) {

            this.timestamp = timestamp;
            return this;
        }

        public BlogPostBuilder withBody(String body) {

            this.body = body;
            return this;
        }

        public BlogPostBuilder withUserId(String userId) {

            this.userId = userId;
            return this;
        }

        public BlogPost build() {

            BlogPost blogPost = new BlogPost(timestamp, body, userId);
            blogPost.id = this.id;
            return blogPost;
        }
    }
}
