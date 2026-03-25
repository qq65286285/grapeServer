# 测试计划分配功能 API文档

## 1. 分配测试计划给成员

### 接口信息
- **请求方式**: POST
- **请求路径**: /api/testPlan/assign
- **Content-Type**: application/json

### 请求参数
| 参数名 | 类型 | 必需 | 描述 |
| :--- | :--- | :--- | :--- |
| planId | Long | 是 | 测试计划ID |
| userIds | List<Long> | 是 | 成员ID列表 |
| assignedBy | Long | 是 | 分配人ID |

### 请求示例
```json
{
  "planId": 1,
  "userIds": [1, 2, 3],
  "assignedBy": 1
}
```

### 响应格式
```json
{
  "code": 0,
  "message": "分配成功",
  "requestId": null,
  "data": null
}
```

## 2. 查看我的已完成任务

### 接口信息
- **请求方式**: GET
- **请求路径**: /api/testPlanTask/myTasks/completed/{userId}

### 路径参数
| 参数名 | 类型 | 必需 | 描述 |
| :--- | :--- | :--- | :--- |
| userId | Long | 是 | 用户ID |

### 响应格式
```json
{
  "code": 0,
  "message": "成功",
  "requestId": null,
  "data": [
    {
      "id": 1,
      "planId": 1,
      "taskNo": "TASK-001",
      "taskName": "测试任务1",
      "taskType": 1,
      "status": 3,
      "priority": 1,
      "ownerId": 1,
      "startTime": "2026-03-01 00:00:00",
      "endTime": "2026-03-07 00:00:00",
      "progress": 100,
      "description": "任务描述",
      "createdBy": 1,
      "createdTime": "2026-02-28 00:00:00",
      "updatedBy": 1,
      "updatedTime": "2026-03-07 00:00:00"
    }
  ]
}
```

## 3. 查看我的未开始任务

### 接口信息
- **请求方式**: GET
- **请求路径**: /api/testPlanTask/myTasks/pending/{userId}

### 路径参数
| 参数名 | 类型 | 必需 | 描述 |
| :--- | :--- | :--- | :--- |
| userId | Long | 是 | 用户ID |

### 响应格式
```json
{
  "code": 0,
  "message": "成功",
  "requestId": null,
  "data": [
    {
      "id": 2,
      "planId": 1,
      "taskNo": "TASK-002",
      "taskName": "测试任务2",
      "taskType": 1,
      "status": 1,
      "priority": 2,
      "ownerId": 1,
      "startTime": "2026-03-08 00:00:00",
      "endTime": "2026-03-14 00:00:00",
      "progress": 0,
      "description": "任务描述",
      "createdBy": 1,
      "createdTime": "2026-02-28 00:00:00",
      "updatedBy": 1,
      "updatedTime": "2026-02-28 00:00:00"
    }
  ]
}
```

## 4. 查看我的进行中任务

### 接口信息
- **请求方式**: GET
- **请求路径**: /api/testPlanTask/myTasks/inProgress/{userId}

### 路径参数
| 参数名 | 类型 | 必需 | 描述 |
| :--- | :--- | :--- | :--- |
| userId | Long | 是 | 用户ID |

### 响应格式
```json
{
  "code": 0,
  "message": "成功",
  "requestId": null,
  "data": [
    {
      "id": 3,
      "planId": 1,
      "taskNo": "TASK-003",
      "taskName": "测试任务3",
      "taskType": 1,
      "status": 2,
      "priority": 1,
      "ownerId": 1,
      "startTime": "2026-03-01 00:00:00",
      "endTime": "2026-03-07 00:00:00",
      "progress": 50,
      "description": "任务描述",
      "createdBy": 1,
      "createdTime": "2026-02-28 00:00:00",
      "updatedBy": 1,
      "updatedTime": "2026-03-03 00:00:00"
    }
  ]
}
```

## 任务状态说明
- 1: 未开始
- 2: 进行中
- 3: 已完成