-- 为 test_plan_execute_step 表添加 snapshot_step_id 字段，用于关联 test_plan_case_snapshot_step 表
ALTER TABLE `test_plan_execute_step` ADD COLUMN `snapshot_step_id` BIGINT COMMENT '快照步骤ID，关联到test_plan_case_snapshot_step表的ID';

-- 添加索引以提高查询性能
ALTER TABLE `test_plan_execute_step` ADD INDEX `idx_snapshot_step_id` (`snapshot_step_id`);

-- 可选：添加外键约束，确保数据完整性
ALTER TABLE `test_plan_execute_step` ADD CONSTRAINT `fk_test_plan_execute_step_snapshot_step` FOREIGN KEY (`snapshot_step_id`) REFERENCES `test_plan_case_snapshot_step` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;