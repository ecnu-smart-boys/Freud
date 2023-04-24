package org.ecnusmartboys.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@EqualsAndHashCode(of = "id", callSuper = false)
@TableName(value = "sys_user", autoResultMap = true)
public class User extends BaseEntity{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
