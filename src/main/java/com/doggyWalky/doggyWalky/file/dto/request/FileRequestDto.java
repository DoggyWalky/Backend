package com.doggyWalky.doggyWalky.file.dto.request;

import com.doggyWalky.doggyWalky.file.common.TableName;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRequestDto {

    private TableName tableName;

    private Long tableKey;

    public static FileRequestDto create(TableName tableName, Long tableKey) {
        return FileRequestDto.builder().tableName(tableName).tableKey(tableKey).build();
    }

}
