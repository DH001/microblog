package com.forgerock.microblog.filter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SortColumnTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void parseValidASC() throws Exception {

        final SortColumn sortCol = SortColumn.parse("colName:ASC");
        assertThat(sortCol.getColumn(), is("colName"));
        assertThat(sortCol.getDirection(), is(SortColumn.SortDirection.ASC));
    }

    @Test
    public void parseValidDESC() throws Exception {

        final SortColumn sortCol = SortColumn.parse("colName:DESC");
        assertThat(sortCol.getColumn(), is("colName"));
        assertThat(sortCol.getDirection(), is(SortColumn.SortDirection.DESC));
    }

    @Test
    public void parseEmpty_IllegalArgument() throws Exception {

        expectedException.expect(IllegalArgumentException.class);
        SortColumn.parse("");
    }

    @Test
    public void parseWrongFormat_IllegalArgument() throws Exception {

        expectedException.expect(IllegalArgumentException.class);
        SortColumn.parse("sortcolumn=ASC"); // missing correct divider
    }
}