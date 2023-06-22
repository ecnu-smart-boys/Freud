package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.UpdatePsdAndAvatarRequest;
import org.ecnusmartboys.application.dto.request.command.UpdateVisitorRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Responses<UserInfo> getUserInfo(Common common);

    Responses<Object> updateVisitorInfo(UpdateVisitorRequest req, Common common);

    /**
     * 把用户踢下线
     *
     * @param userId
     */
    void offline(Long userId);

    /**
     * 保存头像
     */
    Responses<String> saveAvatar(MultipartFile file, Common common);

    /**
     * 修改头像和密码
     */
    Responses<String> updatePsdAndAvatar(UpdatePsdAndAvatarRequest req, Common common);
}
