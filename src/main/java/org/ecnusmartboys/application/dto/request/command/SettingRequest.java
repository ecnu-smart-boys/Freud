package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("会话设置")
public class SettingRequest {

    @NotNull
    @ApiModelProperty("最大会话数量")
    @Range(min = 1, max = 10, message = "最大会话数量必须是一个1-10之间的整数")
    private Integer maxConversations;
}
