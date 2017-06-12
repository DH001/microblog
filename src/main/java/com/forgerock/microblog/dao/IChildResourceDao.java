package com.forgerock.microblog.dao;

import java.util.List;

/**
 * DAO for adding and getting child resources by parent resource
 */
public interface IChildResourceDao<Child> {

    /**
     * Get all child resources by parent id
     *
     * @param parentId Parent Id
     * @return Child resources
     */
    List<Child> getAllByParentId(String parentId);

    /**
     * Add new child resource to parent resource (parent resource id should be inside resource object)
     *
     * @param child    Child resource with no id
     * @return Created child resource with generated child id
     */
    Child addToParentResource(Child child);
}
