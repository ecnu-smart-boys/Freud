package org.ecnusmartboys.model.response;

import lombok.Data;

@Data
public class AccessTokenResponse {

    private String access_token;

    private Long expires_in;
}
