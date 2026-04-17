package com.instagram.postservice.repository;

import com.instagram.postservice.config.DynamoDbProperties;
import com.instagram.postservice.entity.PostEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class DynamoDbPostRepository implements PostRepository {

    private static final Comparator<PostEntity> POST_SORT_KEY_DESC =
            Comparator.comparing(PostEntity::getPostSortKey).reversed();

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
        DynamoDbIndex<PostEntity> index = postTable.index("postId-index");
        for (Page<PostEntity> page : index.query(query -> query.queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(postId).build())))) {
            for (PostEntity post : page.items()) {
                return Optional.of(post);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<PostEntity> findAll() {
        List<PostEntity> posts = new ArrayList<>();
        postTable.scan().items().forEach(posts::add);
        posts.sort(POST_SORT_KEY_DESC);
        return posts;
    }

    @Override
    public List<PostEntity> findByUserId(String userId) {
        List<PostEntity> posts = new ArrayList<>();
        for (Page<PostEntity> page : postTable.query(query -> query.queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(userId).build()))
                .scanIndexForward(false))) {
            posts.addAll(page.items());
        }
        return posts;
    }

    @Override
    public List<PostEntity> findRecentByUserId(String userId, int limit) {
        List<PostEntity> posts = new ArrayList<>();
        for (Page<PostEntity> page : postTable.query(query -> query.queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(userId).build()))
                .scanIndexForward(false)
                .limit(limit))) {
            for (PostEntity post : page.items()) {
                posts.add(post);
                if (posts.size() >= limit) {
                    return posts;
                }
            }
        }
        return posts;
    }

    @Override
    public void deleteById(String postId) {
        findById(postId).ifPresent(postTable::deleteItem);
    }
}
