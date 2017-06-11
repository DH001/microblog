package com.forgerock.microblog.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter and sort options for BlogPost
 */
public class BlogPostSortFilter implements SortFilter {

    private String fromDateTime;
    private String toDateTime;

    // Paging
    private Integer size;
    private Integer offset;

    private List<String> userIds = new ArrayList<>();

    private List<String> sort = new ArrayList<>();

    public BlogPostSortFilter() {
    }

    public BlogPostSortFilter(final String fromDateTime, final String toDateTime, final Integer size, final Integer offset, final List<String> userIds, final List<String> sort) {
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
        this.size = size;
        this.offset = offset;
        this.userIds = userIds;
        this.sort = sort;
    }

    public String getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(final String fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public String getToDateTime() {
        return toDateTime;
    }

    public void setToDateTime(final String toDateTime) {
        this.toDateTime = toDateTime;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(final Integer offset) {
        this.offset = offset;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(final List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(final List<String> sort) {
        this.sort = sort;
    }
}
