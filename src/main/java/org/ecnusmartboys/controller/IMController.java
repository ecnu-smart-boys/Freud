package org.ecnusmartboys.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.config.IMConfig;
import org.ecnusmartboys.model.response.BaseResponse;
import org.ecnusmartboys.utils.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/im")
@Api(tags = "IM即时通信")
public class IMController {

    private final IMConfig imConfig;

    @GetMapping("/userSig")
    public BaseResponse<String> userSig() {
        var userId = SecurityUtil.getCurrentUserId();
        var userSig = imConfig.getUserSig(String.valueOf(userId));
        return BaseResponse.ok("ok", userSig);
    }

    @GetMapping("/cb")
    public BaseResponse<?> callback() {
        return null;
    }
}
