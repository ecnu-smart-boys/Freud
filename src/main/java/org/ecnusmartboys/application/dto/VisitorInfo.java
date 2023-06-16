package org.ecnusmartboys.application.dto;

import lombok.Data;

@Data
public class VisitorInfo extends UserInfo {

    private String emergencyContact;

    private String emergencyPhone;

    private Integer totalTime;
}
