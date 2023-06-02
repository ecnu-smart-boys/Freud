package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultRecordInfo {

    private String id;

    private String visitorName;

    private String consultantName;

    private Long startTime;

    private Long endTime;

    private Integer score;

    private String comment;

    private String helper;
}
