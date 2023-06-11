package org.ecnusmartboys.infrastructure.data.mysql.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 咨询消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "message", autoResultMap = true)
public class MessageDO extends BaseDO {

    private Long id;

    private String msgKey;

    private String conversationId;

    private String fromId;

    private String toId;

    /**
     * JSON格式
     */
    private String msgBody;

    /**
     * 消息发送时间(秒)
     */
    private long time;

    @TableField("is_revoked")
    private boolean revoked;
}
