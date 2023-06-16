package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.UserInfo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxLoginResponse {

    private UserInfo userInfo;
}