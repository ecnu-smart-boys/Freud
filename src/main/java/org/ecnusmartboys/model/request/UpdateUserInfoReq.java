package org.ecnusmartboys.model.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;

import static org.ecnusmartboys.utils.Validator.PATTERN_PHONE_STR;

@Data
@ApiModel("更新用户信息参数")
public class UpdateUserInfoReq {

    @Range(min = 0, message = "年龄不能为负数")
    private Integer age;

    @Length(max = 1023, message = "头像地址过长")
    private String avatar;

    private Integer gender;

    @Length(max = 32, message = "姓名过长")
    private String name;

    @Pattern(regexp = PATTERN_PHONE_STR, message = "手机号格式错误")
    private String phone;

    @Length(max = 32, message = "用户名过长")
    private String username;
}
