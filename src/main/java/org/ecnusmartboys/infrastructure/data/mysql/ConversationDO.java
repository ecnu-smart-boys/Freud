package org.ecnusmartboys.infrastructure.data.mysql;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 咨询记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = ConversationDO.TABLE_NAME, autoResultMap = true)
public class ConversationDO extends BaseDO {
    public static final String TABLE_NAME = "conversation";
    private Long fromId;

    private Long toId;

    private Long startTime;

    private Long endTime;
}