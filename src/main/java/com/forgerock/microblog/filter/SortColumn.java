package com.forgerock.microblog.filter;

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

        String[] splitStr = sortParam.split(SORT_SPLIT_TOKEN);
        return new SortColumn(splitStr[0], splitStr[1]);
    }
}

