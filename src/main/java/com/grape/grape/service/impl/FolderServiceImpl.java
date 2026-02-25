package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.Folder;
import com.grape.grape.mapper.FolderMapper;
import com.grape.grape.service.FolderService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-02-08
 */
@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder>  implements FolderService{

}
