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
@TableName(value = User.TABLE_NAME, autoResultMap = true)
public class User extends BaseModel {
    public static final String TABLE_NAME = "sys_user";

    private String openId;

    private String username;

    private String password;

    private String name;

    private Integer gender;

    private Integer age;

    private String avatar;

    private String phone;

    private String email;

    @TableField("is_disabled")
    private Boolean disabled;

    @TableField(value = "roles", typeHandler = JacksonTypeHandler.class)
    private List<String> roles;

}
