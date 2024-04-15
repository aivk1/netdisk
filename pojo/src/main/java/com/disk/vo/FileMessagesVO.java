package com.disk.vo;

import com.disk.entity.FileMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileMessagesVO {
    private Integer count;
    private List<FileMessage> fileMessages;
}
