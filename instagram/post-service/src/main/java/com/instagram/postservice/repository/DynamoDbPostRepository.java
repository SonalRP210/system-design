package com.instagram.postservice.repository;

import com.instagram.postservice.config.DynamoDbProperties;
import com.instagram.postservice.entity.PostEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Key;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class DynamoDbPostRepository implements PostRepository {

    private static final Comparator<PostEntity> CREATED_AT_DESC =
            Comparator.comparing(PostEntity::getCreatedAt).reversed();

    private final DynamoDbTable<PostEntity> postTable;

    public DynamoDbPostRepository(DynamoDbEnhancedClient enhancedClient, DynamoDbProperties properties) {
        this.postTable = enhancedClient.table(properties.getTableName(), TableSchema.fromBean(PostEntity.class));
    }

    @Override
    public PostEntity save(PostEntity postEntity) {
        postTable.putItem(postEntity);
        return postEntity;
    }

    @Override
    public Optional<PostEntity> findById(String postId) {
        return Optional.ofNullable(postTable.getItem(Key.builder().partitionValue(postId).build()));
    }

    @Override
    public List<PostEntity> findAll() {
        List<PostEntity> posts = new ArrayList<>();
        postTable.scan().items().forEach(posts::add);
        posts.sort(CREATED_AT_DESC);
        return posts;
    }

    @Override
    public List<PostEntity> findByUserId(String userId) {
        Expression filterExpression = Expression.builder()
                .expression("userId = :userId")
                .putExpressionValue(":userId", AttributeValue.builder().s(userId).build())
                .build();

        List<PostEntity> posts = new ArrayList<>();
        postTable.scan(request -> request.filterExpression(filterExpression)).items().forEach(posts::add);
        posts.sort(CREATED_AT_DESC);
        return posts;
    }

    @Override
    public void deleteById(String postId) {
        postTable.deleteItem(Key.builder().partitionValue(postId).build());
    }
}
