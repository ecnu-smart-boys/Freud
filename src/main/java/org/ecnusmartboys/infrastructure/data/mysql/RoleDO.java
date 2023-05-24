package org.ecnusmartboys.infrastructure.data.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = RoleDO.TABLE_NAME, autoResultMap = true)
public class RoleDO extends BaseDO{
    public static final String TABLE_NAME = "user_role";

    public static final String USER_ID = "user_id";
    @TableField(USER_ID)
    private String userID;

    @TableField(ROLE)
    public static final String ROLE = "role";
    private String role;
}
