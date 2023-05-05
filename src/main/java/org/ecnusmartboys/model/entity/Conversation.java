package org.ecnusmartboys.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@EqualsAndHashCode(of = "id", callSuper = false)
@TableName(value = "conversation", autoResultMap = true)
public class Conversation extends BaseEntity{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long consultantId;

    private Long visitorId;

    private Long startTime;

    private Long endTime;
}
