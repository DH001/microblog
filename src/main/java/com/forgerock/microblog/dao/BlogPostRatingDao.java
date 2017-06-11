package com.forgerock.microblog.dao;

import com.forgerock.microblog.es.AbstractElasticsearchDao;
import com.forgerock.microblog.model.BlogPostRating;
import com.google.common.base.Preconditions;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BlogPostRatingDao extends AbstractElasticsearchDao implements IChildResourceDao<BlogPostRating> {

    @Override
    public List<BlogPostRating> getAllByParentId(final String parentId) {

        Preconditions.checkNotNull(parentId, "parentId cannot be null");

        // Filter by blog post (parent) id
        final QueryBuilder queryBuilder = QueryBuilders.commonTermsQuery("blogPostId", Collections.singletonList(parentId));

        // Do search
        final Optional<SearchResponse> resp = findAll(queryBuilder);

        // Handle results
        List<BlogPostRating> results = new ArrayList<>();
        if (resp.isPresent() && resp.get().getHits().getTotalHits() > 0) {
            for (SearchHit hit : resp.get().getHits().getHits()) {
                results.add(GSON.fromJson(hit.getSourceAsString(), BlogPostRating.class));
            }
        }
        return results;
    }

    @Override
    public BlogPostRating addToParentResource(final String blogPostId, final BlogPostRating resourceToCreate) {

        Preconditions.checkNotNull(resourceToCreate, "Blog post rating cannot be null");
        Preconditions.checkNotNull(blogPostId, "Blog Post id cannot be null");
        super.createNewDocument(resourceToCreate.getId(), GSON.toJson(resourceToCreate));
        return resourceToCreate;
    }

    @Override
    protected String getIndex() {

        return "microblog";
    }

    @Override
    protected String getType() {

        return "rating";
    }
}
