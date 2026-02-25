-- 创建测试用例向量同步记录表
CREATE TABLE IF NOT EXISTS `case_vector_sync` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `test_case_id` INT NOT NULL COMMENT '测试用例ID',
  `sync_status` INT NOT NULL DEFAULT 0 COMMENT '同步状态：0-待同步，1-同步成功，2-同步失败',
  `sync_message` VARCHAR(255) DEFAULT NULL COMMENT '同步结果信息',
  `sync_time` BIGINT DEFAULT NULL COMMENT '同步时间',
  `retry_count` INT DEFAULT 0 COMMENT '重试次数',
  `business_type` VARCHAR(20) DEFAULT NULL COMMENT '业务类型：add-新增，update-修改，delete-删除，rollback-回滚',
  `created_at` BIGINT NOT NULL COMMENT '创建时间',
  `updated_at` BIGINT NOT NULL COMMENT '更新时间',
  UNIQUE KEY `uk_test_case_id` (`test_case_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用例向量同步记录';

-- 添加索引
CREATE INDEX idx_sync_status ON `case_vector_sync` (`sync_status`);
CREATE INDEX idx_sync_time ON `case_vector_sync` (`sync_time`);
