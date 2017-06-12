package com.forgerock.microblog.es;

import com.forgerock.microblog.model.DateTimeConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Super class for ES DAOs
 */
public abstract class AbstractElasticsearchDao {

    // For turning results into model objects
    protected static final Gson GSON = new GsonBuilder()
            .setDateFormat(DateTimeConstants.ISO_OFFSET_DATE_TIME)
            .create();

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    protected ElasticsearchClient getClient() {return elasticsearchClient;}

    /**
     * Index name
     */
    protected abstract String getIndex();

    /** Type name */
    protected abstract String getType();

}
