package org.ecnusmartboys.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 咨询评分表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@TableName(value = "rate", autoResultMap = true)
public class Rate extends BaseEntity{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long targetId;

    private Long userId;

    private Integer score;

    private String comment;
}
