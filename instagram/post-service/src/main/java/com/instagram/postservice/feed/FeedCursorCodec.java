package com.instagram.postservice.feed;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class FeedCursorCodec {

    private FeedCursorCodec() {
    }

    public static String encode(String postSortKey) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(postSortKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String cursor) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid feed cursor");
        }
    }
}
