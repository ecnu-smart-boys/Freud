package org.ecnusmartboys.application.dto.request.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecnusmartboys.api.annotation.Timestamp;
import org.ecnusmartboys.infrastructure.utils.Validator;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@ApiModel("查询咨询记录列表参数")
public class ConsultRecordListReq {

    @ApiModelProperty(value = "页码")
    @Min(value = 1, message = "页码不合法")
    private Long current = 1L;

    @ApiModelProperty(value = "页大小")
    @Min(value = 1, message = "页大小不合法")
    private Long size = 5L;

    @ApiModelProperty("名字模糊查询，默认为空")
    @Pattern(regexp = "^[\\p{L}a-zA-Z]{0,32}$", message = "姓名格式不正确")
    private String name = "";

    @ApiModelProperty("日期查询，默认为空")
    @Timestamp
    private Long timestamp;
}
