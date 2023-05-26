package org.ecnusmartboys.infrastructure.data.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = UserDO.TABLE_NAME, autoResultMap = true)
public class UserDO extends BaseDO {
    public static final String TABLE_NAME = "sys_user";

    public static final String OPEN_ID = "open_id";
    private String openId;

    public static final String USERNAME = "username";
    private String username;

    public static final String PASSWORD = "password";
    private String password;

    public static final String NAME = "name";
    private String name;

    public static final String GENDER = "gender";
    private Integer gender;

    public static final String AGE = "age";
    private Integer age;

    public static final String AVATAR = "avatar";
    private String avatar;

    public static final String PHONE = "phone";
    private String phone;

    public static final String EMAIL = "email";
    private String email;

    private String role;

    public static final String DISABLED = "is_disabled";
    @TableField(DISABLED)
    private Boolean disabled;
}
