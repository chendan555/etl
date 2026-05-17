package com.gx.etl.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.datasource.entity.FileServer;
import com.gx.etl.datasource.mapper.FileServerMapper;
import org.springframework.stereotype.Service;

@Service
public class FileServerService extends ServiceImpl<FileServerMapper, FileServer> {

    public Page<FileServer> listPage(int pageIndex, int pageSize) {
        Page<FileServer> page = new Page<>(pageIndex, pageSize);
        return page(page, new LambdaQueryWrapper<FileServer>().orderByDesc(FileServer::getCreateTime));
    }
}
