package org.ecnusmartboys.api;

import org.ecnusmartboys.api.constance.SessionKey;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.Extra;
import org.ecnusmartboys.infrastructure.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;

public class Extractor {
    public static Common extract(HttpServletRequest request){
        var session = request.getSession();
        Common common = new Common();
        common.setUserId((String) session.getAttribute(SessionKey.UserID));
        common.setRole((String) session.getAttribute(SessionKey.Role));

        Extra extra = new Extra();
        extra.setIp(RequestUtil.getIp(request));
        common.setExtra(extra);
        return common;
    }
}
