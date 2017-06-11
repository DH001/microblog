package com.forgerock.microblog.model;

/**
 * A rating (1-5) for a blog post submitted by a user.
 */
public class BlogPostRating {

    // TODO Could be yml config value
    // Highest score for a post
    public static final int MAX_RATING = 5;
    // Lowest score for a post
    public static final int MIN_RATING = 1;

    private String id;

    private String blogPostId;

    private int rating;

    private String userId;

    public BlogPostRating(final String id, final String blogPostId, final int rating, final String userId) {
        this.id = id;
        this.blogPostId = blogPostId;
        this.rating = rating;
        this.userId = userId;
    }

    /**
     * Required by Spring
     */
    public BlogPostRating() {
    }

    public String getId() {

        return id;
    }

    public String getBlogPostId() {

        return blogPostId;
    }

    public int getRating() {

        return rating;
    }

    public String getUserId() {

        return userId;
    }

    public static final class BlogPostRatingBuilder {

        private String id;
        private String blogPostId;
        private int rating;
        private String userId;

        private BlogPostRatingBuilder() {}

        public static BlogPostRatingBuilder aBlogPostRating() { return new BlogPostRatingBuilder();}

        public BlogPostRatingBuilder withId(String id) {

            this.id = id;
            return this;
        }

        public BlogPostRatingBuilder withBlogPostId(String blogPostId) {

            this.blogPostId = blogPostId;
            return this;
        }

        public BlogPostRatingBuilder withRating(int rating) {

            this.rating = rating;
            return this;
        }

        public BlogPostRatingBuilder withUserId(String userId) {

            this.userId = userId;
            return this;
        }

        public BlogPostRating build() {

            BlogPostRating blogPostRating = new BlogPostRating(id, blogPostId, rating, userId);
            return blogPostRating;
        }
    }
}
