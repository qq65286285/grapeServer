package com.grape.grape.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试用例文件夹实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_case_folders")
public class TestCaseFolder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件夹ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 父文件夹ID（根目录为0）
     */
    private Integer parentId;

    /**
     * 层级路径（用于快速查找，如：1/2/3）
     */
    private String path;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间（毫秒级时间戳）
     */
    private Long createdAt;

    /**
     * 更新人ID
     */
    private String updatedBy;

    /**
     * 更新时间（毫秒级时间戳）
     */
    private Long updatedAt;
}
