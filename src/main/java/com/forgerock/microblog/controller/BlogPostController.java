package com.forgerock.microblog.controller;

import com.forgerock.microblog.dao.BlogPostDao;
import com.forgerock.microblog.exception.BadRequestException;
import com.forgerock.microblog.exception.NotFoundException;
import com.forgerock.microblog.filter.BlogPostSortFilter;
import com.forgerock.microblog.model.BlogPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST service for blog posts
 */
@RestController
public class BlogPostController {

    private final static Logger LOG = LoggerFactory.getLogger(BlogPostController.class);

    private final BlogPostDao blogPostDao;

    @Autowired
    public BlogPostController(BlogPostDao blogPostDao) {
        this.blogPostDao = blogPostDao;
    }

    /**
     * Get a list of all the blogPosts.
     * <p>
     * Can be filtered by a date range and/or user ID :
     * e.g. GET /blogposts?userIds=user1,user3&fromDateTime=2010-06-11T15:06:24.627Z&toDateTime=2016-06-11T15:06:24.627Z
     * </p>
     * <p>
     * Can be sorted by a date range and/or user ID :
     * e.g. GET /blogposts?sort=userId:DESC
     * </p>
     * <p>
     * Can be paged if required:
     * e.g. GET /blogposts?size=10&offset=0
     * </p>
     * <p>
     * Note that sort, filter and paging can all be used on the same URL.
     * </p>
     *
     * @param blogPostSortFilter Filter, sort and paging values
     * @return List of blog posts or empty list if none
     */
    @GetMapping
    public List<BlogPost> getAll(BlogPostSortFilter blogPostSortFilter) {

        return blogPostDao.getAll(blogPostSortFilter);
    }

    /**
     * Get a specific BlogPost by its ID.
     * e.g. GET /blogposts/ccafed04-96a2-4a2e-9147-4453f3d309a4
     *
     * @param id ID of the blog post (a GUID)
     * @return A BlogPost or 401 if not found.
     */
    @GetMapping("/blogposts/{id}")
    public BlogPost getBlogPost(@PathVariable(value = "id") final String id) {
        if (StringUtils.isEmpty(id == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: /blogposts/{id}");
        }

        final Optional<BlogPost> response = blogPostDao.getById(id);
        return response.orElseThrow(NotFoundException::new);
    }

    /**
     * Create a new BlogPost.
     * <p>
     * POST /blogposts  <br/>
     * { "body": "Evil plans",  "userId": "DrEvil" }
     * </p>
     *
     * Note that the timestamp and ID will be automatically generated.
     * The URL location of the new BlogPost will be returned in the Location header of the response.
     * @param blogPost New Blog Post data
     * @return 201 if success
     */
    @PostMapping("/blogposts")
    public ResponseEntity createBlogPost(@RequestBody final BlogPost blogPost) {
        // We will accept empty posts as they can be updated later.

        final String newId = UUID.randomUUID().toString();
        final BlogPost created = blogPostDao.create(BlogPost.BlogPostBuilder
                .aBlogPost()
                .withBody(blogPost.getBody())
                .withUserId(blogPost.getUserId())
                .withTimestamp(new Date())
                .withId(newId)
                .build()
        );
        LOG.trace("Created blogpost: {}", created);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        LOG.trace("Built URI for created blogpost: {}", location);
        return ResponseEntity.created(location).build();
    }

    /**
     * Update the body text of an existing POST. UserId and Timestamp cannot be updated and will be ignored if supplied.
     * <p>
     * PUT /blogposts/38d566e9-7af0-4b77-ace4-cd35a2fea4ee  <br/>
     * { "body": "New Evil plans" }
     * </p>
     *
     *
     * @param id ID for post to update. Must exist.
     * @param blogPost Updated data (only body will be updated)
     * @return Updated blog post with HTTP 200. 404 if not found.
     */
    @PutMapping("/blogposts/{id}")
    public ResponseEntity<BlogPost> updateBlogPost(@PathVariable(value = "id") final String id, @RequestBody final BlogPost blogPost) {
        if (StringUtils.isEmpty(id == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: /blogposts/{id}");
        }

        // Only allow edit of post body
        final BlogPost updated = blogPostDao.update(BlogPost.BlogPostBuilder
                .aBlogPost()
                .withId(id)
                // Ignore timestamp and userId by design - we only update the text
                // When user authentication is added we can limit updates to the user who created the BlogPost.
                .withBody(blogPost.getBody())
                .build()
        );
        LOG.trace("Updated blogpost: {}", updated);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete an existing BlogPost. If not found then no action is taken.
     * e.g. DELETE /blogposts/38d566e9-7af0-4b77-ace4-cd35a2fea4ee
     *
     * @param id Id of BlogPost
     * @return HTTP 204
     */
    @DeleteMapping("/blogposts/{id}")
    public ResponseEntity<BlogPost> deleteBlogPost(@PathVariable(value = "id") final String id) {
        if (StringUtils.isEmpty(id == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: /blogposts/{id}");
        }
        blogPostDao.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search for a single text term (whole word match)
     * <p>
     * Usage:  /search?searchTerm={my_term}
     *
     * @param searchTerm Single search term
     * @return Results
     */
    @GetMapping("/search")
    public List<BlogPost> searchOrders(@RequestParam("searchTerm") final String searchTerm) {
        // No point searching with empty string
        if (StringUtils.isEmpty(searchTerm)) {
            throw new BadRequestException("Empty search term");
        }

        // Do search
        return blogPostDao.search(searchTerm);
    }
}
