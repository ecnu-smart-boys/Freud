package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelpRecordInfo {

    private String id;

    private String consultantName;

    private String supervisorName;

    private Long startTime;

    private Long endTime;

}
