package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TOP创建者VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCreatorVO {

    /**
     * 创建者ID
     */
    private String creatorId;

    /**
     * 创建的用例数量
     */
    private Long caseCount;
}