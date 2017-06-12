package com.forgerock.microblog.model;

/**
 * A numeric rating for a blog post submitted by a user.
 */
public class BlogPostRating {

    // TODO Could be yml config value
    // Highest score for a post
    public static final int MAX_RATING = 5;
    // Lowest score for a post
    public static final int MIN_RATING = 1;

    private String id;

    private String blogPostId;

    private Integer rating;

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

    public void setId(final String id) {

        this.id = id;
    }

    public String getBlogPostId() {
        return blogPostId;
    }

    public void setBlogPostId(final String blogPostId) {

        this.blogPostId = blogPostId;
    }

    public Integer getRating() {

        return rating;
    }

    public void setRating(final Integer rating) {

        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {

        this.userId = userId;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof BlogPostRating)) {
            return false;
        }

        final BlogPostRating that = (BlogPostRating) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (blogPostId != null ? !blogPostId.equals(that.blogPostId) : that.blogPostId != null) {
            return false;
        }
        if (rating != null ? !rating.equals(that.rating) : that.rating != null) {
            return false;
        }
        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int hashCode() {

        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (blogPostId != null ? blogPostId.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder("BlogPostRating{");
        sb.append("id='").append(id).append('\'');
        sb.append(", blogPostId='").append(blogPostId).append('\'');
        sb.append(", rating=").append(rating);
        sb.append(", userId='").append(userId).append('\'');
        sb.append('}');
        return sb.toString();
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
