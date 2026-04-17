package com.instagram.postservice.repository;

import com.instagram.postservice.config.DynamoDbProperties;
import com.instagram.postservice.entity.FollowEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class DynamoDbFollowRepository implements FollowRepository {

    private final DynamoDbTable<FollowEntity> followTable;

    public DynamoDbFollowRepository(DynamoDbEnhancedClient enhancedClient, DynamoDbProperties properties) {
        this.followTable =
                enhancedClient.table(properties.getFollowTableName(), TableSchema.fromBean(FollowEntity.class));
    }

    @Override
    public FollowEntity save(FollowEntity follow) {
        followTable.putItem(follow);
        return follow;
    }

    @Override
    public void delete(String followerId, String followedId) {
        followTable.deleteItem(Key.builder()
                .partitionValue(followerId)
                .sortValue(followedId)
                .build());
    }

    @Override
    public List<String> findFollowedUserIds(String followerId) {
        List<String> ids = new ArrayList<>();
        for (Page<FollowEntity> page : followTable.query(query -> query.queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(followerId).build())))) {
            for (FollowEntity entity : page.items()) {
                ids.add(entity.getFollowedId());
            }
        }
        return ids;
    }
}
