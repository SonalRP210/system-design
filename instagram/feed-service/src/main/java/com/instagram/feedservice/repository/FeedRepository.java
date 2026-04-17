package com.instagram.feedservice.repository;

import com.instagram.feedservice.entity.FeedPostEntity;
import java.util.List;

public interface FeedRepository {

    List<FeedPostEntity> findPage(int offset, int limit);

    int count();
}
