package com.forgerock.microblog.dao;

import java.util.List;

/**
 * For Daos that implement text search
 */
public interface ITextSearchDao<T> {

    /**
     * Search for matching text in supported fields
     *
     * @param searchTerm Search term
     * @return Results
     */
    List<T> search(String searchTerm);
}
