package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分布统计VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionVO {

    /**
     * 分组名称（模块名、优先级、文件夹名等）
     */
    private String name;

    /**
     * 该组的用例数量
     */
    private Long count;
}