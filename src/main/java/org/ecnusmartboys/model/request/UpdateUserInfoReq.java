package org.ecnusmartboys.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;

import static org.ecnusmartboys.utils.Validator.PATTERN_PHONE_STR;

@Data
@ApiModel("更新用户信息参数")
public class UpdateUserInfoReq {

    @Range(min = 0, message = "年龄不能为负数")
    @ApiModelProperty("年龄")
    private Integer age;

    @Length(max = 1023, message = "头像地址过长")
    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("性别")
    private Integer gender;

    @Length(max = 32, message = "姓名过长")
    @ApiModelProperty("姓名")
    private String name;

    @Pattern(regexp = PATTERN_PHONE_STR, message = "手机号格式错误")
    @ApiModelProperty("手机号")
    private String phone;

    @Length(max = 32, message = "用户名过长")
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("紧急联系人")
    private String emergencyContact;

    @ApiModelProperty("紧急联系人电话")
    private String emergencyPhone;
}
