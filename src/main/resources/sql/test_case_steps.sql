-- 创建测试用例步骤表
CREATE TABLE IF NOT EXISTS `test_case_steps` (
  `id` INT AUTO_INCREMENT COMMENT '步骤ID' PRIMARY KEY,
  `test_case_id` INT NOT NULL COMMENT '关联的测试用例ID',
  `step_number` INT NOT NULL COMMENT '步骤序号',
  `step` TEXT NOT NULL COMMENT '步骤描述',
  `expected_result` TEXT NOT NULL COMMENT '步骤预期结果',
  `created_by` VARCHAR(255) COMMENT '创建人ID',
  `created_at` BIGINT COMMENT '创建时间（毫秒级时间戳）',
  `updated_by` VARCHAR(255) COMMENT '更新人',
  `updated_at` BIGINT COMMENT '更新时间（毫秒级时间戳）',
  `is_deleted` BIGINT COMMENT '逻辑删除（存储删除时间戳，null表示未删除）',
  INDEX `idx_test_case_id` (`test_case_id`),
  INDEX `idx_step_number` (`step_number`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用例步骤表';
