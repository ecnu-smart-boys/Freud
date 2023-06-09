package org.ecnusmartboys.infrastructure.data.mysql.table;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName(value = CommentDO.TABLE_NAME, autoResultMap = true)
public class CommentDO extends BaseDO {
    public static final String TABLE_NAME = "comment";
    public static final String COMMENTED = "is_commented";
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;
    private Long conversationId;
    private Long userId;
    private Integer score;
    private String text;
    private String tag;
    @TableField(COMMENTED)
    private Boolean commented;
}
