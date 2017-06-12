package com.forgerock.microblog.controller;

import com.forgerock.microblog.dao.BlogPostDao;
import com.forgerock.microblog.dao.BlogPostRatingDao;
import com.forgerock.microblog.exception.BadRequestException;
import com.forgerock.microblog.model.BlogPost;
import com.forgerock.microblog.model.BlogPostRating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST service for blog post ratings
 */
@RestController
public class BlogPostRatingController {

    private static final String REST_URL = "/blogposts/{id}/ratings";

    private final static Logger LOG = LoggerFactory.getLogger(BlogPostRatingController.class);

    private final BlogPostDao blogPostDao;

    private final BlogPostRatingDao blogPostRatingDao;

    @Autowired
    public BlogPostRatingController(BlogPostDao blogPostDao, BlogPostRatingDao blogPostRatingDao) {

        this.blogPostDao = blogPostDao;
        this.blogPostRatingDao = blogPostRatingDao;
    }

    /**
     * Get all the ratings for a specific BlogPost.
     * e.g. GET /blogposts/38d566e9-7af0-4b77-ace4-cd35a2fea4ee/ratings
     *
     * @param id Blog Post Id
     * @return List of ratings and who made them.
     */
    @GetMapping(REST_URL)
    public List<BlogPostRating> getAllRatingsForBlogPost(@PathVariable(value = "id") final String id) {

        if (StringUtils.isEmpty(id == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: " + REST_URL);
        }
        return blogPostRatingDao.getAllByParentId(id);
    }

    /**
     * Submit a new rating for a Blog Post.
     * <p>
     * <p>
     * PUT /blogposts/38d566e9-7af0-4b77-ace4-cd35a2fea4ee/ratings  <br/>
     * {"rating": 4, "userId": "blofeld"}
     * </p>
     *
     * @param blogPostRating A rating from 1 to 5.
     * @param blogPostId     A Blog Post Id
     * @return 201 if successfully created. 404 if Blog Post not found.
     */
    @PostMapping(REST_URL)
    public ResponseEntity submitRatingForBlogPost(@RequestBody final BlogPostRating blogPostRating, @PathVariable(value = "id") String blogPostId) {

        if (blogPostRating.getRating() > BlogPostRating.MAX_RATING || blogPostRating.getRating() < BlogPostRating.MIN_RATING) {
            // Note: Could use JSR-303 bean validation if more complex objects and validation were required but keeping it simple for now.
            throw new BadRequestException(String.format("Submitted 'rating' value must be an integer between %d and %d inclusive ", BlogPostRating.MIN_RATING, BlogPostRating.MAX_RATING));
        }
        if (StringUtils.isEmpty(blogPostId == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: " + REST_URL);
        }

        // If BLog Post Id does  not match an existing blog post then return 404.
        Optional<BlogPost> blogPost = blogPostDao.getById(blogPostId);
        if (!blogPost.isPresent()) {
            ResponseEntity.notFound();
        }

        // Add rating
        final String newId = UUID.randomUUID().toString();
        final BlogPostRating created = blogPostRatingDao.addToParentResource(BlogPostRating.BlogPostRatingBuilder
                .aBlogPostRating()
                .withRating(blogPostRating.getRating())
                .withId(newId)
                .withBlogPostId(blogPostId) // Take from URL in preference to what is in body
                .withUserId(blogPostRating.getUserId())
                .build()
        );
        LOG.trace("Created blog post rating: {}", created);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{ratingId}")
                .buildAndExpand(created.getId(), created.getId()).toUri();
        LOG.trace("Built URI for created blog post rating: {}", location);
        return ResponseEntity.created(location).build();
    }
}
