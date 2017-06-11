package com.forgerock.microblog.dao;

import com.forgerock.microblog.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * CRUD operations for data store
 */
public interface ICrudDao<T, K> {

    /**
     * Get resource by unique id
     *
     * @param id Unique id of post
     * @return Resource matching id or empty if not found
     */
    Optional<T> getById(String id);

    /**
     * Get all resources filtered and sorted with provided parameter
     *
     * @param sortFilter Sort and filter criteria. If null then no sorting or filtering.
     * @return List of sorted resources or empty list if no matches
     */
    List<T> getAll(K sortFilter);

    /**
     * If Id is null then create a new resource with generated id.
     *
     * @param resourceToCreate New resource to create
     * @return Created/updated resource
     */
    T create(T resourceToCreate);

    /**
     * Update an existing resource
     *
     * @param resourceToUpdate Resource with updated fields. Id must be present and exist.
     * @return Updated resource
     * @throws NotFoundException No resource with this id could be found
     */
    T update(T resourceToUpdate) throws NotFoundException;

    /**
     * Delete reosurce with specified id
     *
     * @param id Unique id
     * @throws NotFoundException No resource with this id could be found
     */
    void delete(String id) throws NotFoundException;

}
