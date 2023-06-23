package org.ecnusmartboys.infrastructure.data.mysql.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    private long iterator;

    private String conversationId;

    private Long fromId;

    private Long toId;

    private String msgBody;

    /**
     * 消息发送时间(秒)
     */
    private Date time;

    public static final String REVOKED = "is_revoked";
    @TableField(REVOKED)
    private Boolean revoked;


}
