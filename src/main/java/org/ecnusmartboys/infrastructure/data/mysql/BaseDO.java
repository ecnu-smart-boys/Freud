package org.ecnusmartboys.infrastructure.data.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 所有实体类的基类，包含了所有实体类的公共属性，所有进数据库的实体类应该继承这个类。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDO {

    protected Date createTime;

    protected Date updateTime;

}
