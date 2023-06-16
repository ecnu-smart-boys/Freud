package org.ecnusmartboys.infrastructure.data.mysql.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    public static final String CONSULTATION = "is_consultation";
    public static final String SHOWN = "is_shown";
    @TableId(value = "conversation_id", type = IdType.AUTO)
    protected Long conversationId;
    private Long fromId;
    private Long toId;
    private Date startTime;
    private Date endTime;
    private Long helperId;
    @TableField(CONSULTATION)
    private Boolean isConsultation;
    @TableField(SHOWN)
    private Boolean isShown;
}
