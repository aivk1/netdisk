package com.disk.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChunkResult implements Serializable {
    private static final long serialVersionUID = -9000695051292877324L;
    //是否以上传过
    private boolean skipUpload;
    private List<Long> uploadedChunks;
    private String msg;
    private String Location;


}
