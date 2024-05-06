package com.disk.mapper;

import com.disk.entity.ChunkInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ChunkInfoMapper {
    List<Long> selectChunkNumbers(ChunkInfo chunkInfo);
    List<ChunkInfo> selectChunks(String identifier, String filename);
    @Insert("insert into t_chunk_info(ID," +
            " CHUNK_NUMBER," +
            " CHUNK_SIZE," +
            " CURRENT_CHUNK_SIZE," +
            " IDENTIFIER," +
            "FILENAME," +
            "RELATIVE_PATH," +
            "TOTAL_CHUNKS," +
            "TOTAL_SIZE," +
            "FILE_TYPE ) values(" +
            "#{id}," +
            "#{chunkNumber}," +
            "#{chunkSize}," +
            "#{currentChunkSize}," +
            "#{identifier}," +
            "#{filename}," +
            "#{relativePath}," +
            "#{totalChunks}," +
            "#{totalSize}," +
            "#{fileType})")
    Integer insert(ChunkInfo chunkInfo);
    @Delete("delete from t_chunk_info where IDENTIFIER = #{identifier} AND FILENAME = #{filename}")
    void delete(ChunkInfo chunkInfo);
    @Select("select Id from t_chunk_info order by ID desc;")
    List<Long> selectIds();
}
