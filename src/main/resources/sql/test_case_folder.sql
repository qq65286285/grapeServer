-- 创建测试用例文件夹表
CREATE TABLE IF NOT EXISTS `test_case_folders` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '文件夹ID',
  `name` VARCHAR(255) NOT NULL COMMENT '文件夹名称',
  `parent_id` INT(11) DEFAULT '0' COMMENT '父文件夹ID（根目录为0）',
  `path` VARCHAR(255) DEFAULT '' COMMENT '层级路径（用于快速查找，如：1/2/3）',
  `sort` INT(11) DEFAULT '0' COMMENT '排序',
  `created_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人ID',
  `created_at` BIGINT(20) DEFAULT NULL COMMENT '创建时间（毫秒级时间戳）',
  `updated_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人ID',
  `updated_at` BIGINT(20) DEFAULT NULL COMMENT '更新时间（毫秒级时间戳）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_path` (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用例文件夹表';

-- 向测试用例表添加folder_id字段
ALTER TABLE `test_cases` ADD COLUMN `folder_id` INT(11) DEFAULT '0' COMMENT '所属文件夹ID' AFTER `module`;

-- 添加索引
ALTER TABLE `test_cases` ADD INDEX `idx_folder_id` (`folder_id`);

-- 初始化根文件夹
INSERT INTO `test_case_folders` (`name`, `parent_id`, `path`, `sort`, `created_at`, `updated_at`) 
VALUES ('根目录', 0, '0', 0, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
