package org.ecnusmartboys.model.response;

import lombok.Data;

/**
 * 用于接收微信小程序登录返回的数据
 */
@Data
public class Code2SessionResponse {

    String openid;

    @SuppressWarnings("MemberName")
    String session_key;

    String unionid;

    Integer errcode;

    String errmsg;
}
