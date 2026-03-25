-- 创建测试计划用例快照步骤表
CREATE TABLE IF NOT EXISTS `test_plan_case_snapshot_step` (
  `id` BIGINT AUTO_INCREMENT COMMENT '步骤ID' PRIMARY KEY,
  `snapshot_id` BIGINT NOT NULL COMMENT '关联的测试计划用例快照ID',
  `step_number` INT NOT NULL COMMENT '步骤序号',
  `step_description` TEXT NOT NULL COMMENT '步骤描述',
  `expected_result` TEXT NOT NULL COMMENT '步骤预期结果',
  `actual_result` TEXT COMMENT '步骤实际结果',
  `execute_status` INT DEFAULT 0 COMMENT '步骤执行状态: 0-未执行, 1-通过, 2-失败, 3-阻塞, 4-跳过',
  `remark` VARCHAR(500) COMMENT '步骤备注',
  `created_by` VARCHAR(255) COMMENT '创建人ID',
  `created_at` BIGINT COMMENT '创建时间（毫秒级时间戳）',
  `updated_by` VARCHAR(255) COMMENT '更新人',
  `updated_at` BIGINT COMMENT '更新时间（毫秒级时间戳）',
  `is_deleted` BIGINT COMMENT '逻辑删除（存储删除时间戳，null表示未删除）',
  INDEX `idx_snapshot_id` (`snapshot_id`),
  INDEX `idx_step_number` (`step_number`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试计划用例快照步骤表';