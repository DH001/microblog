package com.forgerock.microblog.dao;

import com.forgerock.microblog.exception.NotFoundException;
import com.forgerock.microblog.filter.BlogPostSortFilter;
import com.forgerock.microblog.model.BlogPost;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BlogPostDao implements ICrudDao<BlogPost, BlogPostSortFilter> {

    @Override
    public Optional<BlogPost> getById(final String id) {

        return null;
    }

    @Override
    public List<BlogPost> getAll(final BlogPostSortFilter sortFilter) {

        return Collections.emptyList();
    }

    @Override
    public BlogPost create(final BlogPost resourceToCreate) {

        return null;
    }

    @Override
    public BlogPost update(final BlogPost resourceToUpdate) throws NotFoundException {

        return null;
    }

    @Override
    public void delete(final String id) throws NotFoundException {

    }
}
