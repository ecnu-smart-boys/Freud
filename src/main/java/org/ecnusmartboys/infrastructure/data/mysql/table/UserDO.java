package org.ecnusmartboys.infrastructure.data.mysql.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@TableName(value = UserDO.TABLE_NAME, autoResultMap = true)
public class UserDO extends BaseDO {
    public static final String TABLE_NAME = "sys_user";
    public static final String OPEN_ID = "open_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String AVATAR = "avatar";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String DISABLED = "is_disabled";
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;
    @TableField(OPEN_ID)
    private String openID;
    private String username;
    private String password;
    private String name;
    private Integer gender;
    private Integer age;
    private String avatar;
    private String phone;
    private String email;
    private String role;
    @TableField(DISABLED)
    private Boolean disabled;
}
