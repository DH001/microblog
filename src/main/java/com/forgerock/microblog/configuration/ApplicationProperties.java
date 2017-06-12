package com.forgerock.microblog.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Holds the YML properties
 */
@Component
@ConfigurationProperties
public class ApplicationProperties {

    private Elasticsearch elasticsearch;

    public Elasticsearch getElasticsearch() {

        return elasticsearch;
    }

    public void setElasticsearch(final Elasticsearch elasticsearch) {

        this.elasticsearch = elasticsearch;
    }

    public static class Elasticsearch {

        private String host;
        private int port;
        private String cluster;

        public String getHost() {

            return host;
        }

        public void setHost(final String host) {

            this.host = host;
        }

        public int getPort() {

            return port;
        }

        public void setPort(final int port) {

            this.port = port;
        }

        public String getCluster() {

            return cluster;
        }

        public void setCluster(final String cluster) {

            this.cluster = cluster;
        }
    }
}
