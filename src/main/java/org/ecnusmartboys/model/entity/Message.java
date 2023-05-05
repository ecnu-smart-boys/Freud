package org.ecnusmartboys.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@EqualsAndHashCode(of = "id", callSuper = false)
@TableName(value = "message", autoResultMap = true)
public class Message extends BaseEntity{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
