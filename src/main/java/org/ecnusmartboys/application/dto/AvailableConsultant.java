package org.ecnusmartboys.application.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableConsultant {

    @ApiModelProperty("咨询师id")
    private String consultantId;

    @ApiModelProperty("咨询师头像")
    private String avatar;

    @ApiModelProperty("咨询师名字")
    private String name;

    @ApiModelProperty("是否咨询过")
    private boolean hasConsulted;

    @ApiModelProperty("咨询师状态")
    private int state;

    @ApiModelProperty("平均评价")
    private int avgComment;
}
