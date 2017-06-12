package com.forgerock.microblog.filter;

import com.google.common.base.Preconditions;
import org.springframework.util.StringUtils;

/**
 * Results column to sort by
 */
public class SortColumn {

    private static final java.lang.String SORT_SPLIT_TOKEN = ":";
    private String column;
    private SortDirection direction;

    public SortColumn(String column, String direction) {
        this.column = column;
        this.direction = SortDirection.valueOf(direction.toUpperCase());
    }

    public String getColumn() {return column;}

    public SortDirection getDirection() {return direction;}

    public enum SortDirection {
        ASC, DESC;
    }

    public static SortColumn parse(String sortParam) {

        Preconditions.checkArgument(!StringUtils.isEmpty(sortParam), "String cannot be empty");
        String[] splitStr = sortParam.split(SORT_SPLIT_TOKEN);
        if (splitStr.length != 2) {
            throw new IllegalArgumentException("Must be the format: sortColumnName:[ASC|DESC]");
        }
        return new SortColumn(splitStr[0], splitStr[1]);
    }
}

