package org.ecnusmartboys.infrastructure.model.wx;

import lombok.Data;

@Data
public class AccessTokenResponse {

    private String access_token;

    private Long expires_in;
}
