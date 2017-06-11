package com.forgerock.microblog.es;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Client for ES calls
 */
public class ElasticsearchRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchRestClient.class);

    private RestClient restClient;

    public ElasticsearchRestClient(RestClient restClient) {

        this.restClient = restClient;
    }

    /**
     * Searches Elasticsearch index/type with the given the given parameters
     *
     * @param path index and type
     * @return Response document
     */
    public String search(final String path, String body) {
        // Create endpoint url
        final String endpoint = String.format("/%s/_search", path);

        try {
            // Invoke endpoint with provided search request
            HttpEntity httpEntity = createHttpRequestEntity(body);
            LOG.debug("{} {} , requestBody: {}", HttpMethod.GET.name(), endpoint, body);
            Response response = restClient.performRequest(HttpMethod.GET.name(), endpoint, Collections.emptyMap(),
                    httpEntity);

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch (IOException e) {
            LOG.error("Search request {} failed with error: ", endpoint, e);
            throw new RuntimeException(e); // Cannot recover to wrap in Runtime exception
        }
    }

    /**
     * Finds Elasticsearch index/type/id
     *
     * @param path index and type
     * @return Response document
     */
    public String getById(final String path) {
        // Create endpoint url
        final String endpoint = String.format("/%s", path);

        try {
            // Invoke endpoint with provided search request
            LOG.debug("{} {}", HttpMethod.GET.name(), endpoint);
            Response response = restClient.performRequest(HttpMethod.GET.name(), endpoint, Collections.emptyMap());

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch (IOException e) {
            LOG.error("Get request {} failed with error: ", endpoint, e);
            throw new RuntimeException(e); // Cannot recover to wrap in Runtime exception
        }
    }


    /**
     * Creates new Elasticsearch Document
     *
     * @param path index and type
     * @return Response document
     */
    public String create(final String path, String body) {
        // Create endpoint url
        final String endpoint = String.format("/%s", path);

        try {
            // Invoke endpoint with provided search request
            HttpEntity httpEntity = createHttpRequestEntity(body);
            LOG.debug("{} {} , requestBody: {}", HttpMethod.POST.name(), endpoint, body);
            Response response = restClient.performRequest(HttpMethod.POST.name(), endpoint, Collections.emptyMap(),
                    httpEntity);

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch (IOException e) {
            LOG.error("Create request {} failed with error: ", endpoint, e);
            throw new RuntimeException(e); // Cannot recover to wrap in Runtime exception
        }
    }

    /**
     * Updates existing Elasticsearch document
     *
     * @param path index and type
     * @return Response document
     */
    public String update(final String path, String body) {
        // Create endpoint url
        final String endpoint = String.format("/%s/_update", path);

        try {
            // Invoke endpoint with provided search request
            HttpEntity httpEntity = createHttpRequestEntity(body);
            LOG.debug("{} {} , requestBody: {}", HttpMethod.POST.name(), endpoint, body);
            Response response = restClient.performRequest(HttpMethod.POST.name(), endpoint, Collections.emptyMap(),
                    httpEntity);

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch (IOException e) {
            LOG.error("Update request {} failed with error: ", endpoint, e);
            throw new RuntimeException(e); // Cannot recover to wrap in Runtime exception
        }
    }

    /**
     * Deletes Elasticsearch document
     *
     * @param path index and type
     * @return Response document
     */
    public String delete(final String path) {
        // Create endpoint url
        final String endpoint = String.format("/%s/_update", path);

        try {
            // Invoke endpoint with provided search request
            LOG.debug("{} {}", HttpMethod.DELETE.name(), endpoint);
            Response response = restClient.performRequest(HttpMethod.DELETE.name(), endpoint, Collections.emptyMap());

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch (IOException e) {
            LOG.error("Delete request {} failed with error: ", endpoint, e);
            throw new RuntimeException(e); // Cannot recover to wrap in Runtime exception
        }
    }

    private HttpEntity createHttpRequestEntity(String body) {

        StringEntity httpEntity = new StringEntity(body, StandardCharsets.UTF_8);
        httpEntity.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return httpEntity;
    }

    /**
     * Closes the internal Rest Client
     */
    public void close() {

        LOG.debug("Shutting down Elasticsearch client...");
        try {
            restClient.close();
            LOG.info("Elasticsearch client successfully shutdown");
        }
        catch (IOException e) {
            LOG.error("Failed to cleanup Elasticsearch client", e);
        }
    }
}
