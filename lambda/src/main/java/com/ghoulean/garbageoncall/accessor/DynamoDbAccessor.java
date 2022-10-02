package com.ghoulean.garbageoncall.accessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Slf4j
@Singleton
public final class DynamoDbAccessor {
    private static final String PARTITION_KEY = "id";
    private static final String PARTITION_VALUE = "0";
    private static final String INDICES_KEY = "indices";

    private @NonNull final DynamoDbClient dynamoDB;
    private @NonNull final String tableName;

    @Inject
    public DynamoDbAccessor(@NonNull final DynamoDbClient dynamoDB, @Named("tableName") final String tableName) {
        this.dynamoDB = dynamoDB;
        this.tableName = tableName;
    }

    public List<String> getItem() {
        log.info("getItem");
        final HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();
        keyToGet.put(PARTITION_KEY, AttributeValue.builder()
                .n(PARTITION_VALUE).build());
        final GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();
        try {
            final Map<String, AttributeValue> item = dynamoDB.getItem(request).item();
            if (item != null) {
                final List<String> indicesList = item.get(INDICES_KEY).l().stream()
                        .map(AttributeValue::n)
                        .collect(Collectors.toList());
                return indicesList;
            } else {
                throw new RuntimeException("Could not find ddb entry");
            }
        } catch (DynamoDbException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateItemInTable(final List<String> indicesList) {
        log.info("putItemInTable");
        HashMap<String, AttributeValue> itemKey = new HashMap<String, AttributeValue>();
        itemKey.put(PARTITION_KEY, AttributeValue.builder()
                .n(PARTITION_VALUE).build());

        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
        updatedValues.put(INDICES_KEY, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().l(indicesList.stream()
                        .map(index -> AttributeValue.fromN(index))
                        .collect(Collectors.toList())).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();

        try {
            dynamoDB.updateItem(request);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DynamoDbException e) {
            throw new RuntimeException(e);
        }
        log.info("The Amazon DynamoDB table was updated!");
    }
}
