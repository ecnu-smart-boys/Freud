package org.ecnusmartboys.model.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短信验证码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SMSCode {

    /**
     * 实际的六位验证码
     */
    private String code;

    /**
     * 后端随机生成的UUID，用于区分验证码
     */
    private String codeId;
}
