package com.forgerock.microblog.controller;

import com.forgerock.microblog.dao.BlogPostDao;
import com.forgerock.microblog.model.BlogPost;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BlogPostController.class)
public class BlogPostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BlogPostDao dao;

    @Test
    public void testGetAllPosts_returnList() throws Exception {
        // Setup data and mock
        Instant now = Instant.now();
        final List<BlogPost> blogPostList = Arrays.asList(
                BlogPost.BlogPostBuilder.aBlogPost().withBody("Text1").withId("1").withTimestamp(now).withUserId("user1").build(),
                BlogPost.BlogPostBuilder.aBlogPost().withBody("Text2").withId("2").withTimestamp(now).withUserId("user2").build()
        );
        Mockito.when(dao.getAll(Mockito.any())).thenReturn(blogPostList);

        // Do test
        this.mockMvc.perform(get("/blogposts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].body").value("Text1"))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].body").value("Text2"))
                .andExpect(jsonPath("$[1].userId").value("user2"));
    }

    @Test
    public void testGetPostsById_returnSpecificPost() throws Exception {
        // Setup data and mock
        Instant now = Instant.now();
        final BlogPost blogPost = BlogPost.BlogPostBuilder.aBlogPost().withBody("Text1").withId("1").withTimestamp(now).withUserId("user1").build();

        Mockito.when(dao.getById(Mockito.eq("1"))).thenReturn(Optional.of(blogPost));

        // Do test
        this.mockMvc.perform(get("/blogposts/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.body").value("Text1"))
                .andExpect(jsonPath("$.userId").value("user1"));
    }

    @Test
    public void testGetPostsById_notFound_return404() throws Exception {

        Mockito.when(dao.getById(Mockito.any())).thenReturn(Optional.empty());

        // Do test
        this.mockMvc.perform(get("/blogposts/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreateBLogPost_success_return201() throws Exception {

        Instant now = Instant.now();
        final BlogPost blogPost = BlogPost.BlogPostBuilder.aBlogPost().withBody("New Post Text").withId("1").withTimestamp(now).withUserId("user1").build();
        Mockito.when(dao.create(Mockito.any())).thenReturn(blogPost);

        // Do test
        this.mockMvc.perform(post("/blogposts")
                .content(new Gson().toJson("New Post Text"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateBlogPost_success_return200() throws Exception {

        Instant now = Instant.now();
        final BlogPost blogPost = BlogPost.BlogPostBuilder.aBlogPost().withBody("New Post Text").withId("1").withTimestamp(now).withUserId("user1").build();
        Mockito.when(dao.update(Mockito.any())).thenReturn(blogPost);

        // Do test
        this.mockMvc.perform(put("/blogposts/1")
                .content(new Gson().toJson("New Post Text"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteBlogPost_success_return204() throws Exception {
        // Do test
        this.mockMvc.perform(delete("/blogposts/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(dao, Mockito.times(1)).delete("1");
    }
}