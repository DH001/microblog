package com.forgerock.microblog.controller;

import com.forgerock.microblog.dao.BlogPostRatingDao;
import com.forgerock.microblog.exception.BadRequestException;
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
import java.util.UUID;

/**
 * REST service for blog post ratings
 */
@RestController
public class BlogPostRatingController {

    private static final String REST_URL = "/blogposts/{id}/ratings";

    private final static Logger LOG = LoggerFactory.getLogger(BlogPostRatingController.class);

    private final BlogPostRatingDao blogPostRatingDao;

    @Autowired
    public BlogPostRatingController(BlogPostRatingDao blogPostRatingDao) {
        this.blogPostRatingDao = blogPostRatingDao;
    }

    @GetMapping(REST_URL)
    public List<BlogPostRating> getAllRatingsForBlogPost(@PathVariable(value = "id") final String id) {

        if (StringUtils.isEmpty(id == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: " + REST_URL);
        }
        return blogPostRatingDao.getAllByParentId(id);
    }

    @PostMapping(REST_URL)
    public ResponseEntity submitRatingForBlogPost(@RequestBody final BlogPostRating blogPostRating, @PathVariable(value = "id") String blogPostId) {

        if (blogPostRating.getRating() > BlogPostRating.MAX_RATING || blogPostRating.getRating() < BlogPostRating.MIN_RATING) {
            // Note: Could use JSR-303 bean validation if more complex objects and validation were required but keeping it simple for now.
            throw new BadRequestException(String.format("Submitted 'rating' value must be an integer between %d and %d inclusive ", BlogPostRating.MIN_RATING, BlogPostRating.MAX_RATING));
        }
        if (StringUtils.isEmpty(blogPostId == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: " + REST_URL);
        }

        final String newId = UUID.randomUUID().toString();
        final BlogPostRating created = blogPostRatingDao.addToParentResource(blogPostId, BlogPostRating.BlogPostRatingBuilder
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
