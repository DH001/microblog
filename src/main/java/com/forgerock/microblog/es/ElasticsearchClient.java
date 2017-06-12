package com.forgerock.microblog.es;

import com.google.common.base.Preconditions;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Gives a singleton instance of the Elasticsearch client
 */
@Component
public class ElasticsearchClient {

    // Could be yml config value if required
    private static int ES_MAX_SIZE = 10000; // Maximum allowed size with default ES install

    private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchClient.class);

    // TODO Could go in yml file
    private static final String host = "elasticsearch";
    private static final int port = 9300; // Transport client port
    private static final String cluster = "elasticsearch"; // Transport client port

    private static final String CLUSTER_NAME_KEY = "cluster.name";

    private TransportClient transportClient;

    @PostConstruct
    public void init() {
        final Settings settings = Settings.builder()
                .put(CLUSTER_NAME_KEY, cluster).build();
        try {
            LOG.debug("Connecting to ElasticSearch with host: {} , post: {} , cluster: {}", host, port, cluster);
            transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
            LOG.info("Connected to ElasticSearch on: {}:{} , cluster: {}", host, port, cluster);

        }
        catch (UnknownHostException e) {
            LOG.error("Unable to initialise Elasticsearch client with unknown host: {}:{}. Check the configuration. ", host, port);
            throw new RuntimeException(e); // Cannot recover from this - fail startup
        }
    }

    /**
     * GET by id
     * @param index Index
     * @param type Type
     * @param id ID
     * @return Get Response or empty
     */
    public Optional<GetResponse> getById(final String index, final String type, final String id) {

        LOG.debug("About to GET /{}/{}/{}", index, type, id);
        try {
            return Optional.ofNullable(
                    transportClient.prepareGet(index, type, id).get()
            );
        }
        catch (IndexNotFoundException indexNotFoundException) {
            // No data created yet in ES. Return empty list.
            LOG.warn("No data created yet for index: {}. Index will be created when new data is posted.", index);
            return Optional.empty();
        }
    }

    /**
     * Delete by ID
     *
     * @param index Index
     * @param type  Type
     * @param id    ID
     */
    public void deleteById(final String index, final String type, final String id) {

        LOG.debug("About to DELETE /{}/{}/{}", index, type, id);
        try {
            transportClient
                    .prepareDelete(index, type, id)
                    .get();
        }
        catch (IndexNotFoundException indexNotFoundException) {
            // No data created yet in ES. Ignore.
            LOG.warn("No data created yet for index: {}. Index will be created when new data is posted.", index);
        }
    }

    public String create(final String index, final String type, final String id, final String json) {

        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(json, "json cannot be null");

        LOG.debug("About to PUT /{}/{}/{}", index, type, id);
        return transportClient
                .prepareIndex(index, type, id)
                .setSource(json, XContentType.JSON)
                .get()
                .getId();
    }

    public void update(final String index, final String type, final String id, final String json) {

        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(json, "json cannot be null");

        LOG.debug("About to PUT /{}/{}/{}", index, type, id);
        transportClient
                .prepareUpdate(index, type, id)
                .setDoc(json, XContentType.JSON)
                .get();
    }

    /**
     * Find all requests with specified filters
     *
     * @param queryBuilder Filters
     * @return Result Json
     */
    public Optional<SearchResponse> getAll(final String index, final String type, final QueryBuilder queryBuilder) {
        return this.getAll(index, type, null, null, queryBuilder, Collections.emptyList());
    }

    /**
     * Find all requests with specified filters, page and sort
     *
     * @param index        Index
     * @param type         Type
     * @param from         Paging offset
     * @param size         page size
     * @param queryBuilder Filters
     * @param sortBuilders Sorts
     * @return Search response or empty
     */
    public Optional<SearchResponse> getAll(final String index, final String type, final Integer from, final Integer size, final QueryBuilder queryBuilder, final List<SortBuilder> sortBuilders) {

        LOG.debug("About to search /{}/{} , filters: {}", index, type, queryBuilder);

        // Build search
        final SearchRequestBuilder searchRequestBuilder = transportClient
                .prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

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
            LOG.warn("No data created yet for index: {}. Index will be created when new data is posted.", index);
            return Optional.empty();
        }
    }

    /**
     * Shutdown when server stops
     */
    @PreDestroy
    public void cleanUp() {
        transportClient.close();
        LOG.info("Elasticsearch client successfully shutdown");
    }


}
