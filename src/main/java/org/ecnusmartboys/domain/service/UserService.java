package org.ecnusmartboys.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.infrastructure.model.mysql.User;
import org.ecnusmartboys.application.dto.request.command.WxRegisterReq;

public interface UserService extends IService<User> {

    String ROLE_VISITOR = "visitor";
    String ROLE_CONSULTANT = "consultant";
    String ROLE_SUPERVISOR = "supervisor";
    String ROLE_ADMIN = "admin";

    User saveVisitor(WxRegisterReq req);

    /**
     * 获取用户信息
     * @param id 用户id
     * @return 用户信息
     */
    UserInfo getUserInfo(Long id);
}
