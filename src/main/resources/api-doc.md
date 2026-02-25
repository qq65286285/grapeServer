# API 接口文档

## 1. 接口概述

本文档描述了对外提供的 API 接口，用于外部系统与本系统进行交互。

## 2. 基础信息

- **服务地址**：`http://localhost:8209/api`
- **请求方式**：RESTful API
- **响应格式**：JSON
- **认证方式**：无需认证（当前系统配置为所有请求都允许访问）

## 3. 接口列表

| 接口路径 | 请求方法 | 接口描述 |
|---------|---------|--------|
| `/upload` | POST | 上传文件到 Minio 存储桶 |

## 4. 详细接口说明

### 4.1 上传文件到 Minio 存储桶

#### 4.1.1 接口信息
- **接口路径**：`/api/upload`
- **请求方法**：POST
- **接口描述**：上传文件到指定的 Minio 存储桶

#### 4.1.2 请求参数

| 参数名 | 类型 | 是否必选 | 描述 |
|--------|------|----------|------|
| `file` | MultipartFile | 是 | 要上传的文件 |
| `bucketName` | String | 是 | Minio 存储桶名称 |

#### 4.1.3 请求示例

**cURL 示例**：

```bash
curl -X POST "http://localhost:8209/api/upload" \
  -F "file=@/path/to/file.txt" \
  -F "bucketName=my-bucket"
```

**Postman 示例**：
1. 选择 POST 请求方法
2. 输入 URL：`http://localhost:8209/api/upload`
3. 在 "Body" 选项卡中选择 "form-data"
4. 添加字段：
   - 字段名：`file`，类型：`File`，值：选择要上传的文件
   - 字段名：`bucketName`，类型：`Text`，值：存储桶名称（例如 "my-bucket"）

#### 4.1.4 返回格式

**成功返回**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "oldFileName": "example.txt",
    "newFileName": "20260211123456789012345.txt",
    "fileUrl": "http://minio-server:9000/my-bucket/20260211123456789012345.txt"
  }
}
```

**失败返回**：

```json
{
  "code": 1,
  "msg": "错误信息",
  "data": null
}
```

#### 4.1.5 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|---------|------|
| 1 | 文件不能为空 | 未上传文件或文件为空 |
| 1 | 存储桶名称不能为空 | 未提供存储桶名称 |
| 1 | 上传失败 | 文件上传过程中发生错误 |

## 5. 代码示例

### 5.1 Java 代码示例

```java
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;

public class ApiClient {
    public static void main(String[] args) throws Exception {
        // 创建 HTTP 客户端
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 POST 请求
            HttpPost httpPost = new HttpPost("http://localhost:8209/api/upload");
            
            // 构建多部分请求实体
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            
            // 添加文件参数
            File file = new File("path/to/file.txt");
            builder.addBinaryBody(
                "file",
                file,
                ContentType.APPLICATION_OCTET_STREAM,
                file.getName()
            );
            
            // 添加存储桶名称参数
            builder.addTextBody("bucketName", "my-bucket", ContentType.TEXT_PLAIN);
            
            // 设置请求实体
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            
            // 执行请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 获取响应状态码
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Status Code: " + statusCode);
                
                // 获取响应内容
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Body: " + responseBody);
            }
        }
    }
}
```

### 5.2 Python 代码示例

```python
import requests

# 文件路径
file_path = "path/to/file.txt"
# 存储桶名称
bucket_name = "my-bucket"
# API 接口地址
url = "http://localhost:8209/api/upload"

# 构建请求参数
files = {
    'file': open(file_path, 'rb')
}
data = {
    'bucketName': bucket_name
}

# 发送请求
response = requests.post(url, files=files, data=data)

# 打印响应结果
print("Status Code:", response.status_code)
print("Response:", response.json())
```

### 5.3 JavaScript 代码示例（使用 Fetch API）

```javascript
// HTML 表单
/*
<form id="uploadForm">
    <input type="file" name="file" id="fileInput">
    <input type="text" name="bucketName" value="my-bucket">
    <button type="submit">上传</button>
</form>
<div id="result"></div>
*/

// JavaScript 代码
document.getElementById('uploadForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const resultDiv = document.getElementById('result');
    
    try {
        const response = await fetch('http://localhost:8209/api/upload', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        resultDiv.innerHTML = JSON.stringify(data, null, 2);
    } catch (error) {
        resultDiv.innerHTML = '上传失败: ' + error.message;
    }
});
```

## 6. 注意事项

1. **文件大小限制**：上传文件的大小受 Minio 服务器配置和网络带宽的限制，请根据实际情况调整。
2. **存储桶存在性**：接口会自动创建不存在的存储桶，但建议提前创建好存储桶以确保权限正确。
3. **文件名生成**：系统会自动生成带时间戳和随机数的文件名，确保文件名唯一。
4. **响应时间**：大文件上传可能需要较长时间，请设置合理的超时时间。
5. **错误处理**：请根据接口返回的错误信息进行相应的错误处理。

## 7. 版本历史

| 版本 | 更新日期 | 更新内容 |
|------|---------|--------|
| 1.0 | 2026-02-11 | 初始版本，添加文件上传接口 |
