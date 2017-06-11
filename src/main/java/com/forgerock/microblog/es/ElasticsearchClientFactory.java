package com.forgerock.microblog.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Gives a singleton instance of the Elasticsearch client
 */
@Component
public class ElasticsearchClientFactory {

    private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchClientFactory.class);

    // TODO Could go in yml file
    private static final String host = "localhost";
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
     * Get client
     *
     * @return ES Client
     */
    public Client getClient() {

        return transportClient;
    }

    /**
     *
     */
    @PreDestroy
    public void cleanUp() {

        transportClient.close();
        LOG.info("Elasticsearch client successfully shutdown");
    }
}
