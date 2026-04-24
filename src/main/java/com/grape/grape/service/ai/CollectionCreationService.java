package com.grape.grape.service.ai;

import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionCreationService {

    @Autowired
    private MilvusClient milvusClient;

    @Value("${milvus.collection.shards.num}")
    private int shardsNum;

    @Value("${milvus.collection.vector.field}")
    private String vectorFieldName;

    @Value("${milvus.collection.vector.dimension}")
    private int vectorDimension;

    @Value("${milvus.collection.primary.key}")
    private String primaryKeyField;

    @Value("${milvus.collection.varchar.max.length}")
    private int varcharMaxLength;

    @Value("${milvus.collection.varchar.medium.length}")
    private int varcharMediumLength;

    /**
     * 创建集合
     * @param collectionName 集合名称
     * @param clazz Java对象类型
     * @return 是否创建成功
     */
    public boolean createCollection(String collectionName, Class<?> clazz) {
        try {
            // 检查集合是否存在
            HasCollectionParam hasParam = HasCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();
            R<Boolean> hasResponse = milvusClient.hasCollection(hasParam);
            if (hasResponse.getData()) {
                System.out.println("Collection already exists: " + collectionName);
                return true;
            }

            // 生成字段列表
            List<FieldType> fieldTypes = generateFieldTypes(clazz);
            if (fieldTypes.isEmpty()) {
                System.out.println("No fields generated from class: " + clazz.getName());
                return false;
            }

            // 创建集合
            CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldTypes(fieldTypes)
                    .withShardsNum(shardsNum)
                    .build();

            R<RpcStatus> createResponse = milvusClient.createCollection(createParam);
            if (createResponse.getStatus() != R.Status.Success.getCode()) {
                System.out.println("Failed to create collection: " + createResponse.getMessage());
                return false;
            }

            // 创建向量索引
            if (vectorFieldName != null && !vectorFieldName.isEmpty()) {
                System.out.println("Creating index for field: " + vectorFieldName);
                CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withFieldName(vectorFieldName)
                        .withIndexName("default_index")
                        .withExtraParam("{\"metric_type\": \"COSINE\"}")
                        .build();

                R<RpcStatus> indexResponse = milvusClient.createIndex(indexParam);
                if (indexResponse.getStatus() != R.Status.Success.getCode()) {
                    System.out.println("Failed to create index: " + indexResponse.getMessage());
                    // 索引创建失败不影响集合创建
                } else {
                    System.out.println("Index created successfully: default_index");
                }
            }

            // 加载集合到内存
            LoadCollectionParam loadParam = LoadCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();

            R<RpcStatus> loadResponse = milvusClient.loadCollection(loadParam);
            if (loadResponse.getStatus() != R.Status.Success.getCode()) {
                System.out.println("Failed to load collection: " + loadResponse.getMessage());
                // 加载失败不影响集合创建
            }

            System.out.println("Collection created successfully: " + collectionName);
            return true;

        } catch (Exception e) {
            System.out.println("Error creating collection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成字段列表
     * @param clazz Java对象类型
     * @return 字段列表
     */
    private List<FieldType> generateFieldTypes(Class<?> clazz) {
        List<FieldType> fieldTypes = new ArrayList<>();

        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            // 跳过静态字段
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // 根据字段类型创建FieldType
            FieldType fieldTypeObj = createFieldType(fieldName, fieldType);
            if (fieldTypeObj != null) {
                fieldTypes.add(fieldTypeObj);
            }
        }

        return fieldTypes;
    }

    /**
     * 将驼峰命名转换为下划线分隔的形式
     * @param camelCase 驼峰命名的字符串
     * @return 下划线分隔的字符串
     */
    private String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 根据Java类型创建FieldType
     * @param fieldName 字段名称
     * @param fieldType Java字段类型
     * @return FieldType对象
     */
    private FieldType createFieldType(String fieldName, Class<?> fieldType) {
        // 将驼峰命名转换为下划线分隔的形式
        String snakeCaseFieldName = camelToSnake(fieldName);
        
        // 主键字段
        if (fieldName.equals(primaryKeyField)) {
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(true)
                    .build();
        }

        // 向量字段
        if (fieldName.equals(vectorFieldName)) {
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.FloatVector)
                    .withDimension(vectorDimension)
                    .build();
        }

        // 特殊处理priority字段，确保它是VarChar类型
        if (fieldName.equals("priority")) {
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.VarChar)
                    .withMaxLength(10)
                    .build();
        }

        // 字符串类型
        if (fieldType == String.class) {
            // 根据字段名称判断使用哪个长度
            int maxLength = varcharMaxLength;
            if (fieldName.equals("precondition") || fieldName.equals("testSteps") || fieldName.equals("expectedResult")) {
                maxLength = varcharMediumLength;
            }
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.VarChar)
                    .withMaxLength(maxLength)
                    .build();
        }

        // 整数类型
        if (fieldType == int.class || fieldType == Integer.class) {
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.Int64)
                    .build();
        }

        // 长整数类型
        if (fieldType == long.class || fieldType == Long.class) {
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.Int64)
                    .build();
        }

        // 浮点数类型
        if (fieldType == float.class || fieldType == Float.class) {
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.Float)
                    .build();
        }

        // 双精度浮点数类型
        if (fieldType == double.class || fieldType == Double.class) {
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.Double)
                    .build();
        }

        // 布尔类型
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            return FieldType.newBuilder()
                    .withName(snakeCaseFieldName)
                    .withDataType(DataType.Bool)
                    .build();
        }

        // 其他类型暂时不支持
        System.out.println("Unsupported field type: " + fieldType.getName() + " for field: " + fieldName);
        return null;
    }

    /**
     * 删除集合
     * @param collectionName 集合名称
     * @return 是否删除成功
     */
    public boolean dropCollection(String collectionName) {
        try {
            // 检查集合是否存在
            HasCollectionParam hasParam = HasCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();
            R<Boolean> hasResponse = milvusClient.hasCollection(hasParam);
            if (!hasResponse.getData()) {
                System.out.println("Collection does not exist: " + collectionName);
                return true;
            }

            // 删除集合
            DropCollectionParam dropParam = DropCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();

            R<RpcStatus> dropResponse = milvusClient.dropCollection(dropParam);
            if (dropResponse.getStatus() != R.Status.Success.getCode()) {
                System.out.println("Failed to drop collection: " + dropResponse.getMessage());
                return false;
            }

            System.out.println("Collection dropped successfully: " + collectionName);
            return true;

        } catch (Exception e) {
            System.out.println("Error dropping collection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}