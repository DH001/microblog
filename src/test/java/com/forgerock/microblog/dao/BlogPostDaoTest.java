package com.forgerock.microblog.dao;

import com.forgerock.microblog.es.ElasticsearchClient;
import com.forgerock.microblog.exception.NotFoundException;
import com.forgerock.microblog.filter.BlogPostSortFilter;
import com.forgerock.microblog.model.BlogPost;
import com.forgerock.microblog.model.DateTimeConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BlogPostDaoTest {

    private static final String ID = "gdkjagdjkhgjhg";
    private static final String BODY = "text";
    private static final String USER = "testuser";

    // For turning results into model objects
    protected static final Gson GSON = new GsonBuilder()
            .setDateFormat(DateTimeConstants.ISO_OFFSET_DATE_TIME)
            .create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private BlogPostDao daoUnderTest;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Before
    public void startup() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getById_resourceWasFound() throws Exception {

        final BlogPost expectedBlogPost = BlogPost.BlogPostBuilder.aBlogPost().withId(ID).withBody(BODY).withUserId(USER).build();

        // Mocks
        final GetResponse getResponse = Mockito.mock(GetResponse.class);
        when(getResponse.getSourceAsString()).thenReturn(GSON.toJson(expectedBlogPost));
        when(elasticsearchClient.getById(any(), any(), any())).thenReturn(Optional.of(getResponse));

        // Do Test
        final Optional<BlogPost> resp = daoUnderTest.getById(ID);

        // Check response
        assertThat(resp.isPresent(), is(true));
        assertThat(resp.get(), is(expectedBlogPost));
        verify(elasticsearchClient).getById(eq(daoUnderTest.getIndex()), eq(daoUnderTest.getType()), eq(ID));
    }

    @Test
    public void getById_resourceNotFound() throws Exception {
        // Mocks
        final GetResponse getResponse = Mockito.mock(GetResponse.class);
        when(elasticsearchClient.getById(any(), any(), any())).thenReturn(Optional.empty());

        // Do Test
        final Optional<BlogPost> resp = daoUnderTest.getById(ID);

        // Check response
        assertThat(resp.isPresent(), is(false));
        verify(elasticsearchClient).getById(eq(daoUnderTest.getIndex()), eq(daoUnderTest.getType()), eq(ID));
    }


    @Test
    public void getAll_oneResult_returnList() throws Exception {

        final BlogPost expectedBlogPost = BlogPost.BlogPostBuilder.aBlogPost().withId(ID).withBody(BODY).withUserId(USER).build();

        // Mocks (A lot of mocking here - possibly refactor response processing if more time...)
        final SearchResponse response = Mockito.mock(SearchResponse.class);
        final SearchHit searchHit = Mockito.mock(SearchHit.class);
        final SearchHits searchHits = Mockito.mock(SearchHits.class);
        when(response.getHits()).thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.getHits()).thenReturn(new SearchHit[] { searchHit });
        when(searchHit.getSourceAsString()).thenReturn(GSON.toJson(expectedBlogPost));
        when(elasticsearchClient.getAll(any(), any(), any(), any(), any(), anyList())).thenReturn(Optional.of(response));

        // Do Test
        BlogPostSortFilter filter = BlogPostSortFilter.BlogPostSortFilterBuilder.aBlogPostSortFilter().build();
        final List<BlogPost> resp = daoUnderTest.getAll(filter);

        // Check response
        assertThat(resp.size(), is(1));
        assertThat(resp.get(0), is(expectedBlogPost));
    }

    @Test
    public void getAll_noResults_returnEmptyList() throws Exception {

        final BlogPost expectedBlogPost = BlogPost.BlogPostBuilder.aBlogPost().withId(ID).withBody(BODY).withUserId(USER).build();

        // Mocks (A lot of mocking here - possibly refactor response processing if more time...)
        final SearchResponse response = Mockito.mock(SearchResponse.class);
        final SearchHits searchHits = Mockito.mock(SearchHits.class);
        when(response.getHits()).thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(0L);
        when(searchHits.getHits()).thenReturn(new SearchHit[] { });
        when(elasticsearchClient.getAll(any(), any(), any(), any(), any(), anyList())).thenReturn(Optional.of(response));

        // Do Test
        BlogPostSortFilter filter = BlogPostSortFilter.BlogPostSortFilterBuilder.aBlogPostSortFilter().build();
        final List<BlogPost> resp = daoUnderTest.getAll(filter);

        // Check response
        assertThat(resp.size(), is(0));
    }

    @Test
    public void create_checkClient_success() throws Exception {

        final BlogPost newBlogPost = BlogPost.BlogPostBuilder.aBlogPost().withId(ID).withBody(BODY).withUserId(USER).build();

        daoUnderTest.create(newBlogPost);
        verify(elasticsearchClient, times(1)).create(daoUnderTest.getIndex(), daoUnderTest.getType(), ID, GSON.toJson(newBlogPost));
    }

    @Test
    public void update_checkClient_success() throws Exception {

        final BlogPost changedBlogPost = BlogPost.BlogPostBuilder.aBlogPost().withId(ID).withBody(BODY).withUserId(USER).build();

        // Mocks needed for Get operation
        final GetResponse getResponse = Mockito.mock(GetResponse.class);
        when(getResponse.getSourceAsString()).thenReturn(GSON.toJson(changedBlogPost));
        when(elasticsearchClient.getById(any(), any(), any())).thenReturn(Optional.of(getResponse));

        daoUnderTest.update(changedBlogPost);
        verify(elasticsearchClient, times(1)).update(daoUnderTest.getIndex(), daoUnderTest.getType(), ID, GSON.toJson(changedBlogPost));
    }

    @Test
    public void update_doesntExist_NotFoundException() throws Exception {

        final BlogPost changedBlogPost = BlogPost.BlogPostBuilder.aBlogPost().withId(ID).withBody(BODY).withUserId(USER).build();

        // Mocks needed for empty Get operation
        final GetResponse getResponse = Mockito.mock(GetResponse.class);
        when(getResponse.getSourceAsString()).thenReturn(GSON.toJson(changedBlogPost));
        when(elasticsearchClient.getById(any(), any(), any())).thenReturn(Optional.empty());

        expectedException.expect(NotFoundException.class);
        daoUnderTest.update(changedBlogPost);
        verifyZeroInteractions(elasticsearchClient);
    }

    @Test
    public void delete_checkClient_success() throws Exception {

        daoUnderTest.delete(ID);
        verify(elasticsearchClient, times(1)).deleteById(daoUnderTest.getIndex(), daoUnderTest.getType(), ID);
    }

}