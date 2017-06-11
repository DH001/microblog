package com.forgerock.microblog.es;

import com.forgerock.microblog.DateTimeConstants;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Super class for ES DAOs
 */
public abstract class AbstractElasticsearchDao {

    // TODO Could be yml config value
    protected static int ES_MAX_SIZE = 10000; // Maximum allowed size with default ES install

    private final static Logger LOG = LoggerFactory.getLogger(AbstractElasticsearchDao.class);

    // For turning results into model objects
    protected static final Gson GSON = new GsonBuilder()
            .setDateFormat(DateTimeConstants.ISO_OFFSET_DATE_TIME)
            .create();

    @Autowired
    private ElasticsearchClientFactory elasticsearchClientFactory;

    protected Optional<GetResponse> findById(final String id) {

        LOG.debug("About to GET /{}/{}/{}", getIndex(), getType(), id);
        try {
            return Optional.ofNullable(
                    elasticsearchClientFactory.getClient()
                            .prepareGet(getIndex(), getType(), id)
                            .get()
            );
        }
        catch (IndexNotFoundException indexNotFoundException) {
            // No data created yet in ES. Return empty list.
            LOG.warn("No data created yet for index: {}. Index will be created when new data is posted.", getIndex());
            return Optional.empty();
        }
    }

    protected void deleteById(final String id) {

        LOG.debug("About to DELETE /{}/{}/{}", getIndex(), getType(), id);
        try {
            elasticsearchClientFactory.getClient()
                    .prepareDelete(getIndex(), getType(), id)
                    .get();
        }
        catch (IndexNotFoundException indexNotFoundException) {
            // No data created yet in ES. Ignore.
            LOG.warn("No data created yet for index: {}. Index will be created when new data is posted.", getIndex());
        }
    }

    protected String createNewDocument(final String id, final String json) {

        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(json, "json cannot be null");

        LOG.debug("About to PUT /{}/{}/{}", getIndex(), getType(), id);
        return elasticsearchClientFactory.getClient()
                .prepareIndex(getIndex(), getType(), id)
                .setSource(json, XContentType.JSON)
                .get()
                .getId();
    }

    protected void updateDocument(final String id, final String json) {

        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(json, "json cannot be null");

        LOG.debug("About to PUT /{}/{}/{}", getIndex(), getType(), id);
        elasticsearchClientFactory.getClient()
                .prepareUpdate(getIndex(), getType(), id)
                .setDoc(json, XContentType.JSON)
                .get();
    }

    /**
     * Convenience method for findAll
     *
     * @param queryBuilder Query criteria
     * @return Result Json
     */
    protected Optional<SearchResponse> findAll(QueryBuilder queryBuilder) {

        return this.findAll(null, null, queryBuilder, Collections.emptyList());
    }

    /**
     * Find all requests with specified filters, page and sort
     *
     * @param from         Pagination start position
     * @param size         Pagination end position
     * @param queryBuilder Query criteria
     * @param sortBuilders Sort criteria
     * @return Result JSON
     */
    protected Optional<SearchResponse> findAll(Integer from, Integer size, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders) {
        LOG.debug("About to search /{}/{} , filters: {}", getIndex(), getType(), queryBuilder);

        // Build search
        final SearchRequestBuilder searchRequestBuilder = elasticsearchClientFactory.getClient()
                .prepareSearch(getIndex().toLowerCase())
                .setTypes(getType().toLowerCase())
                .setSearchType(getSearchType());

        if (from != null) {
            searchRequestBuilder.setFrom(from);
        }
        else {
            searchRequestBuilder.setFrom(0);
        }

        if (size != null) {
            searchRequestBuilder.setSize(size);
        }
        else {
            searchRequestBuilder.setSize(ES_MAX_SIZE);
        }

        // Add filters
        searchRequestBuilder.setQuery(queryBuilder);

        // Add sort
        sortBuilders.forEach(searchRequestBuilder::addSort);

        // Do search
        try {
            return Optional.ofNullable(searchRequestBuilder.get());
        }
        catch (IndexNotFoundException indexNotFoundException) {
            // No data created yet in ES. Return empty list.
            LOG.warn("No data created yet for index: {}. Index will be created when new data is posted.", getIndex());
            return Optional.empty();
        }

    }

    /**
     * Index name
     */
    protected abstract String getIndex();

    /** Type name */
    protected abstract String getType();

    /**
     * Override if different search type is needed for special case
     * @return Search type
     */
    SearchType getSearchType() {return SearchType.DFS_QUERY_THEN_FETCH;}

}
