package com.disk.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecycleFileInfoMapper {
    void insert(List<Long> ids);
}
