package com.instagram.postservice.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.instagram.postservice.api.dto.FeedResponse;
import com.instagram.postservice.api.dto.PostResponse;
import com.instagram.postservice.security.UserIdentityResolver;
import com.instagram.postservice.service.FeedService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FeedController.class)
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedService feedService;

    @MockBean
    private UserIdentityResolver userIdentityResolver;

    @Test
    void getFeedShouldReturnPostsAndCursor() throws Exception {
        when(userIdentityResolver.resolveUserId(eq("alice"), isNull())).thenReturn("alice");
        when(feedService.getHomeFeed(eq("alice"), isNull(), eq(20)))
                .thenReturn(new FeedResponse(
                        List.of(new PostResponse(
                                "p1",
                                "bob",
                                "hi",
                                "https://img/1",
                                "2026-01-02T00:00:00Z",
                                "2026-01-02T00:00:00Z")),
                        "next-cursor-token"));

        mockMvc.perform(get("/feed")
                        .header("X-User-Id", "alice")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].postId").value("p1"))
                .andExpect(jsonPath("$.posts[0].userId").value("bob"))
                .andExpect(jsonPath("$.nextCursor").value("next-cursor-token"));
    }

    @Test
    void getFeedShouldPassCursorAndLimit() throws Exception {
        when(userIdentityResolver.resolveUserId(any(), any())).thenReturn("viewer");
        when(feedService.getHomeFeed(eq("viewer"), eq("abc"), eq(5)))
                .thenReturn(new FeedResponse(List.of(), null));

        mockMvc.perform(get("/feed")
                        .header("X-User-Id", "viewer")
                        .param("cursor", "abc")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts").isEmpty());
    }
}
