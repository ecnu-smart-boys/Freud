package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.ecnusmartboys.adaptor.annotation.Phone;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;

@Data
@ApiModel("更新访客信息参数")
public class UpdateVisitorRequest {

    @Range(min = 0, message = "年龄不能为负数")
    @Range(min = 10, max = 100, message = "年龄必须在10-100之间")
    @ApiModelProperty("年龄")
    private Integer age;

    @Length(max = 1023, message = "头像地址过长")
    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("性别")
    @Range(min = 1, max = 2, message = "性别只能取值为 1 或 2")
    private Integer gender;

    @Length(max = 32, message = "姓名过长")
    @ApiModelProperty("姓名")
    private String nickName;

    @Phone
    @ApiModelProperty("手机号")
    private String phone;

    @Email(message = "邮箱格式错误")
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("紧急联系人")
    private String emergencyContact;

    @ApiModelProperty("紧急联系人电话")
    @Phone
    private String emergencyPhone;
}
