package org.ecnusmartboys.application.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserInfo {

    private Long id;

    private String username;

    private String name;

    private Integer gender;

    private Integer age;

    private String avatar;

    private String phone;

    private String email;

    private Boolean disabled;

    private List<String> roles;

    //访客信息
    private String emergencyContactName;

    private String emergencyContactPhone;
    //咨询师/督导信息
    private String idNumber;

    @ApiModelProperty("工作单位")
    private String department;

    @ApiModelProperty("职称")
    private String title;

    @ApiModelProperty("资质")
    private String qualification;

    @ApiModelProperty("资质编号")
    private String qualificationCode;

    private Integer consultNum;

    private Long accumulatedTime;

    private List<String> supervisors;
}
