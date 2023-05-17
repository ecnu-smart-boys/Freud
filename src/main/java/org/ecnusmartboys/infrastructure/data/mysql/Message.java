package org.ecnusmartboys.infrastructure.data.mysql;

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
public class Message extends BaseModel {
    private String msgKey;

    private Long conversationId;

    private Long fromId;

    private Long toId;

    /**
     * JSON格式
     */
    private String msgBody;

    private Date time;
}
