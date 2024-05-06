package com.disk.service;

import com.disk.entity.ChunkInfo;
import com.disk.result.ChunkResult;

import javax.servlet.http.HttpServletResponse;

public interface ChunkInfoService {
    /**
     * 校检当前文件
     * @param chunkInfo
     * @param response
     * @return
     */
    ChunkResult checkChunkState(ChunkInfo chunkInfo, HttpServletResponse response);

    /**
     * 上传文件
     * @param chunk
     * @return
     */
    Integer uploadFile(ChunkInfo chunk);
}
