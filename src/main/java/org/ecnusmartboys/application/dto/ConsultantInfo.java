package org.ecnusmartboys.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConsultantInfo extends UserInfo{

    private String idNumber;

    private String department;

    private String title;

    private Integer arrangement;

    private Integer avgComment;

    private Integer totalTime;

    private Integer consultTimes;

    private List<StaffBaseInfo> supervisorList;

}
