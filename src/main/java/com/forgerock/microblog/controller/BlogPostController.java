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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST service for blog posts
 */
@RestController
@RequestMapping("/blogposts")
public class BlogPostController {

    private final static Logger LOG = LoggerFactory.getLogger(BlogPostController.class);

    private final BlogPostDao blogPostDao;

    @Autowired
    public BlogPostController(BlogPostDao blogPostDao) {

        this.blogPostDao = blogPostDao;
    }

    @GetMapping
    private List<BlogPost> getAll(BlogPostSortFilter blogPostSortFilter) {

        return blogPostDao.getAll(blogPostSortFilter);
    }

    @GetMapping("/{id}")
    private BlogPost getBlogPost(@PathVariable(value = "id") final String id) {

        if (StringUtils.isEmpty(id == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: /blogposts/{id}");
        }

        final Optional<BlogPost> response = blogPostDao.getById(id);
        return response.orElseThrow(NotFoundException::new);
    }

    @PostMapping("/")
    private ResponseEntity createBlogPost(@RequestBody final String blogPostText) {
        // We will accept empty posts as they can be updated later.

        final String newId = UUID.randomUUID().toString();
        final BlogPost created = blogPostDao.create(BlogPost.BlogPostBuilder
                .aBlogPost()
                .withBody(blogPostText)
                .withTimestamp(Instant.now())
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

    @PutMapping("/{id}")
    private ResponseEntity<BlogPost> updateBlogPost(@PathVariable(value = "id") final String id, @RequestBody final String blogPostText) {

        if (StringUtils.isEmpty(id == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: /blogposts/{id}");
        }

        // Only allow edit of post body
        final BlogPost updated = blogPostDao.update(BlogPost.BlogPostBuilder
                .aBlogPost()
                .withBody(blogPostText)
                .build()
        );
        LOG.trace("Updated blogpost: {}", updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<BlogPost> deleteBlogPost(@PathVariable(value = "id") final String id) {

        if (StringUtils.isEmpty(id == null)) {
            throw new BadRequestException("Missing id field in URL. Usage: /blogposts/{id}");
        }
        blogPostDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
