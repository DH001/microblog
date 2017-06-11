package com.forgerock.microblog.dao;

import com.forgerock.microblog.model.BlogPostRating;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BlogPostRatingDao implements IChildResourceDao<BlogPostRating> {

    @Override
    public List<BlogPostRating> getAllByParentId(final String parentId) {

        return Collections.emptyList();
    }

    @Override
    public BlogPostRating addToParentResource(final String parentId, final BlogPostRating blogPostRating) {

        return null;
    }
}
