package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SupervisorInfo extends UserInfo {

    private String idNumber;

    private String department;

    private String title;

    private String qualification;

    private String qualificationCode;

    private Integer arrangement;

    private Integer totalTime;

    private Integer consultTimes;

    private List<StaffBaseInfo> consultantList;
}
