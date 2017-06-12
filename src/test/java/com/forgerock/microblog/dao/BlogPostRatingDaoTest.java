package com.forgerock.microblog.dao;

import com.forgerock.microblog.es.ElasticsearchClient;
import com.forgerock.microblog.model.BlogPostRating;
import com.forgerock.microblog.model.DateTimeConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BlogPostRatingDaoTest {

    private static final String ID = "hjgjhgjhgjh";
    private static final String PARENT_ID = "gdkjagdjkhgjhg";
    private static final int RATING = 4;
    private static final String USER = "testuser";

    // For turning results into model objects
    protected static final Gson GSON = new GsonBuilder()
            .setDateFormat(DateTimeConstants.ISO_OFFSET_DATE_TIME)
            .create();

    @Before
    public void startup() {

        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private BlogPostRatingDao daoUnderTest;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Test
    public void getAllByParentId_returnListOfOne() throws Exception {

        final BlogPostRating blogPostRating = BlogPostRating.BlogPostRatingBuilder.aBlogPostRating().withId(ID).withBlogPostId(PARENT_ID).withRating(RATING).withUserId(USER).build();

        // Mocks (A lot of mocking here - possibly refactor response processing if more time...)
        final SearchResponse response = Mockito.mock(SearchResponse.class);
        final SearchHit searchHit = Mockito.mock(SearchHit.class);
        final SearchHits searchHits = Mockito.mock(SearchHits.class);
        when(response.getHits()).thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.getHits()).thenReturn(new SearchHit[] { searchHit });
        when(searchHit.getSourceAsString()).thenReturn(GSON.toJson(blogPostRating));
        when(elasticsearchClient.getAll(any(), any(), any())).thenReturn(Optional.of(response));

        // Do test
        final List<BlogPostRating> ratingList = daoUnderTest.getAllByParentId(PARENT_ID);

        // Check response
        assertThat(ratingList.size(), is(1));
        assertThat(ratingList.get(0), is(blogPostRating));
    }

    @Test
    public void getAllByParentId_noResults_returnEmptyList() throws Exception {

        // Mocks (A lot of mocking here - possibly refactor response processing if more time...)
        final SearchResponse response = Mockito.mock(SearchResponse.class);
        final SearchHits searchHits = Mockito.mock(SearchHits.class);
        when(response.getHits()).thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(0L);
        when(elasticsearchClient.getAll(any(), any(), any())).thenReturn(Optional.empty());

        // Do test
        final List<BlogPostRating> ratingList = daoUnderTest.getAllByParentId(PARENT_ID);

        // Check response
        assertThat(ratingList.size(), is(0));
    }

    @Test
    public void addToParentResource() throws Exception {

        final BlogPostRating blogPostRating = BlogPostRating.BlogPostRatingBuilder.aBlogPostRating().withId(ID).withBlogPostId(PARENT_ID).withRating(RATING).withUserId(USER).build();

        // Do test
        daoUnderTest.addToParentResource(blogPostRating);

        // Check calls to client
        verify(elasticsearchClient, times(1)).create(daoUnderTest.getIndex(), daoUnderTest.getType(), ID, GSON.toJson(blogPostRating));
    }

    @Test
    public void addToParentResource_nullId_reject() throws Exception {

        final BlogPostRating blogPostRating = BlogPostRating.BlogPostRatingBuilder.aBlogPostRating().withId(null) // Null id
                .withBlogPostId(PARENT_ID).withRating(RATING).withUserId(USER).build();

        // Do test
        expectedException.expect(NullPointerException.class);
        daoUnderTest.addToParentResource(blogPostRating);
    }

    @Test
    public void addToParentResource_nullParentId_reject() throws Exception {

        final BlogPostRating blogPostRating = BlogPostRating.BlogPostRatingBuilder.aBlogPostRating().withId(ID).withBlogPostId(null) // Null parent id
                .withRating(RATING).withUserId(USER).build();

        // Do test
        expectedException.expect(NullPointerException.class);
        daoUnderTest.addToParentResource(blogPostRating);
    }

}