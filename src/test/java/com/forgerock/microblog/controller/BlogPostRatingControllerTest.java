package com.forgerock.microblog.controller;

import com.forgerock.microblog.dao.BlogPostRatingDao;
import com.forgerock.microblog.model.BlogPostRating;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BlogPostRatingController.class)
public class BlogPostRatingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BlogPostRatingDao dao;

    @Test
    public void testGetAllPosts_returnList() throws Exception {
        // Setup data and mock
        final List<BlogPostRating> blogPostRatingsList = Arrays.asList(
                BlogPostRating.BlogPostRatingBuilder.aBlogPostRating().withId("10").withBlogPostId("1").withUserId("user1").withRating(5).build(),
                BlogPostRating.BlogPostRatingBuilder.aBlogPostRating().withId("11").withBlogPostId("1").withUserId("user2").withRating(1).build()
        );
        Mockito.when(dao.getAllByParentId(Mockito.any())).thenReturn(blogPostRatingsList);

        // Do test
        this.mockMvc.perform(get("/blogposts/1/ratings"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id").value("10"))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].blogPostId").value("1"))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].id").value("11"))
                .andExpect(jsonPath("$[1].rating").value(1))
                .andExpect(jsonPath("$[0].blogPostId").value("1"))
                .andExpect(jsonPath("$[1].userId").value("user2"));
    }

    @Test
    public void testGetAllPosts_handleEmptyList() throws Exception {
        // Setup data and mock
        final List<BlogPostRating> blogPostRatingsList = Collections.emptyList();
        Mockito.when(dao.getAllByParentId(Mockito.any())).thenReturn(blogPostRatingsList);

        // Do test
        this.mockMvc.perform(get("/blogposts/1/ratings"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAddRatingToPost_validRating_return204() throws Exception {
        // Setup data and mock
        BlogPostRating rating = BlogPostRating.BlogPostRatingBuilder.aBlogPostRating().withId("10").withBlogPostId("1").withUserId("user1").withRating(5).build();
        Mockito.when(dao.addToParentResource(Mockito.eq("1"), Mockito.any())).thenReturn(rating);

        // Do test
        this.mockMvc.perform(post("/blogposts/1/ratings")
                .content(new Gson().toJson(5))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testAddRatingToPost_tooHighRating_return400() throws Exception {
        // Do test
        this.mockMvc.perform(post("/blogposts/1/ratings")
                .content(new Gson().toJson(6))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddRatingToPost_tooLowRating_return400() throws Exception {
        // Do test
        this.mockMvc.perform(post("/blogposts/1/ratings")
                .content(new Gson().toJson(0))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}