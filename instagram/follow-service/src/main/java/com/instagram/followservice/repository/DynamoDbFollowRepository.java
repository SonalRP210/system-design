package com.instagram.followservice.repository;

import com.instagram.followservice.config.DynamoDbProperties;
import com.instagram.followservice.entity.FollowEntity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class DynamoDbFollowRepository implements FollowRepository {

    private final DynamoDbTable<FollowEntity> followTable;

    public DynamoDbFollowRepository(DynamoDbEnhancedClient enhancedClient, DynamoDbProperties properties) {
        this.followTable = enhancedClient.table(properties.getTableName(), TableSchema.fromBean(FollowEntity.class));
    }

    @Override
    public FollowEntity save(FollowEntity followEntity) {
        followTable.putItem(followEntity);
        return followEntity;
    }

    @Override
    public boolean delete(String followerId, String followedId) {
        if (!exists(followerId, followedId)) {
            return false;
        }

        followTable.deleteItem(Key.builder()
                .partitionValue(followerId)
                .sortValue(followedId)
                .build());
        return true;
    }

    @Override
    public boolean exists(String followerId, String followedId) {
        return followTable.getItem(Key.builder()
                .partitionValue(followerId)
                .sortValue(followedId)
                .build()) != null;
    }

    @Override
    public List<String> findFollowingByFollowerId(String followerId) {
        return followTable.query(request -> request.queryConditional(
                        software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
                                .keyEqualTo(Key.builder().partitionValue(followerId).build())))
                .items()
                .stream()
                .map(FollowEntity::getFollowedId)
                .sorted()
                .toList();
    }

    @Override
    public List<String> findFollowersByFollowedId(String followedId) {
        Expression filterExpression = Expression.builder()
                .expression("followedId = :followedId")
                .putExpressionValue(":followedId", AttributeValue.builder().s(followedId).build())
                .build();

        return followTable.scan(request -> request.filterExpression(filterExpression))
                .items()
                .stream()
                .map(FollowEntity::getFollowerId)
                .sorted()
                .toList();
    }

    @Override
    public int countUsers() {
        Set<String> users = new HashSet<>();
        followTable.scan().items().forEach(item -> users.add(item.getFollowerId()));
        return users.size();
    }

    @Override
    public int countRelationships() {
        return (int) followTable.scan().items().stream().count();
    }
}
