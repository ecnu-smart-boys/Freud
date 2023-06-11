package org.ecnusmartboys.application.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("显示在求助列表里的字段")
public class HelpRecordInfo {

    private String id;

    private String consultantName;

    private String supervisorName;

    private Long startTime;

    private Long endTime;

}
