package org.ecnusmartboys.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 顾问与督导关系表，多对多
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"consultantId", "supervisorId"}, callSuper = false)
@TableName(value = "consulvisor", autoResultMap = true)
public class Consulvisor extends BaseEntity{

    private Long consultantId;

    private Long supervisorId;
}
