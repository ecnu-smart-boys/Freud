package org.ecnusmartboys.infrastructure.data.mysql;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 评分表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = Comment.TABLE_NAME, autoResultMap = true)
public class Comment extends BaseModel {
    public static final String TABLE_NAME = "conversation_comment";
    private Long conversationId;

    private Long userId;

    private Integer score;

    private String text;
}
