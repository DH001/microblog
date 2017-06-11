package com.forgerock.microblog.dao;

import com.forgerock.microblog.es.AbstractElasticsearchDao;
import com.forgerock.microblog.exception.NotFoundException;
import com.forgerock.microblog.filter.BlogPostSortFilter;
import com.forgerock.microblog.filter.SortColumn;
import com.forgerock.microblog.model.BlogPost;
import com.google.common.base.Preconditions;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

@Component
public class BlogPostDao extends AbstractElasticsearchDao implements ICrudDao<BlogPost, BlogPostSortFilter> {

    @Override
    public Optional<BlogPost> getById(final String id) {

        Preconditions.checkNotNull(id, "id cannot be null");
        Optional<GetResponse> resp = super.findById(id);//.getSourceAsString();
        if (!resp.isPresent() || StringUtils.isEmpty(resp.get().getSourceAsString())) {
            return Optional.empty();
        }
        else {
            return Optional.of(GSON.fromJson(resp.get().getSourceAsString(), BlogPost.class));
        }
    }

    @Override
    public List<BlogPost> getAll(final BlogPostSortFilter sortFilter) {

        Preconditions.checkNotNull(sortFilter, "sortFilter cannot be null");

        Optional<SearchResponse> resp = findAll(sortFilter.getOffset(), sortFilter.getSize(),
                getQueryBuilder(sortFilter),
                getSortBuilders(sortFilter));
        List<BlogPost> results = new ArrayList<>();
        if (resp.isPresent() && resp.get().getHits().getTotalHits() > 0) {
            for (SearchHit hit : resp.get().getHits().getHits()) {
                results.add(GSON.fromJson(hit.getSourceAsString(), BlogPost.class));
            }
        }
        return results;
    }

    private QueryBuilder getQueryBuilder(BlogPostSortFilter filter) {

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (filter != null) {
            if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) {
                boolQueryBuilder = boolQueryBuilder.must(termsQuery("userId", filter.getUserIds()));
            }
            if (filter.getFromDateTime() != null || filter.getToDateTime() != null) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders
                        .rangeQuery("timestamp");
                if (filter.getFromDateTime() != null) {
                    rangeQueryBuilder.gte(filter.getFromDateTime());
                }
                if (filter.getToDateTime() != null) {
                    rangeQueryBuilder.lte(filter.getToDateTime());
                }
                boolQueryBuilder.must(rangeQueryBuilder);
            }
        }

        return QueryBuilders.constantScoreQuery(boolQueryBuilder);
    }

    private List<SortBuilder> getSortBuilders(final BlogPostSortFilter sortFilter) {

        if (sortFilter.getSort() == null || sortFilter.getSort().isEmpty()) {
            return Collections.emptyList();
        }

        return sortFilter.getSort().stream().map(SortColumn::parse).map(this::aSortBuilder).collect(Collectors.toList());
    }

    private SortBuilder aSortBuilder(final SortColumn sortColumn) {

        final org.elasticsearch.search.sort.SortBuilder sortBuilder = new FieldSortBuilder(sortColumn.getColumn());
        sortBuilder.order(SortOrder.valueOf(sortColumn.getDirection().name()));
        return sortBuilder;
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

    @Override
    protected String getIndex() {

        return "microblog";
    }

    @Override
    protected String getType() {

        return "blogpost";
    }
}
