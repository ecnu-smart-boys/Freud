package org.ecnusmartboys.application.dto.enums;

/**
 * 枚举基类，所有枚举类都应该继承此类。
 */
public interface BaseEnum {

    /**
     * 每一个枚举代表的字符串值，用于json解析以及数据库映射。
     *
     * @return /
     */
    String getTextValue();

    /**
     * 每一个枚举的描述，用于前端展示。
     *
     * @return 枚举的描述
     */
    String getDescription();
}
