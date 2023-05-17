package org.ecnusmartboys.infrastructure.data.wx;

import lombok.Data;

@Data
public class AccessTokenResponse {

    private String access_token;

    private Long expires_in;
}
