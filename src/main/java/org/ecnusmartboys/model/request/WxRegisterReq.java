package org.ecnusmartboys.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static org.ecnusmartboys.utils.Validator.PATTERN_PHONE_STR;

@Data
@ApiModel("微信访客注册请求")
public class WxRegisterReq {

    @NotNull(message = "访客姓名不能为空")
    @ApiModelProperty(value = "访客姓名", required = true)
    private String name;

    @NotNull(message = "访客性别不能为空")
    @ApiModelProperty(value = "访客性别", required = true)
    private Integer gender;

    @Range(min = 0, message = "访客年龄必须在大于之间")
    @NotNull(message = "访客年龄不能为空")
    @ApiModelProperty(value = "访客年龄", required = true)
    private Integer age;

    @ApiModelProperty("访客头像")
    private String avatar;

    @NotNull(message = "访客电话不能为空")
    @Pattern(regexp = PATTERN_PHONE_STR, message = "访客电话格式错误")
    @ApiModelProperty(value = "访客电话", required = true)
    private String phone;

    @NotNull(message = "短信验证码不能为空")
    @ApiModelProperty(value = "短信验证码", required = true)
    private String smsCode;

    @NotNull(message = "短信验证码ID不能为空")
    @ApiModelProperty(value = "短信验证码ID", required = true)
    private String smsCodeId;

    @NotNull(message = "访客紧急联系人不能为空")
    @Length(max = 31, message = "访客紧急联系人过长")
    @ApiModelProperty(value = "访客紧急联系人", required = true)
    private String emergencyContact;

    @NotNull(message = "访客紧急联系人电话不能为空")
    @Pattern(regexp = PATTERN_PHONE_STR, message = "访客紧急联系人电话格式错误")
    @ApiModelProperty(value = "访客紧急联系人", required = true)
    private String emergencyPhone;

    @NotNull(message = "微信code不能为空")
    @ApiModelProperty(value = "微信code", required = true)
    private String code;

}
