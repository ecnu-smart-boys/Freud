package org.ecnusmartboys.application.service.impl;

import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import io.github.doocs.im.ImClient;
import io.github.doocs.im.model.request.KickRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.convertor.*;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.enums.OnlineState;
import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.UpdatePsdAndAvatarRequest;
import org.ecnusmartboys.application.dto.request.command.UpdateVisitorRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.application.service.OnlineStateService;
import org.ecnusmartboys.application.service.UserService;
import org.ecnusmartboys.domain.model.user.Consultant;
import org.ecnusmartboys.domain.model.user.Supervisor;
import org.ecnusmartboys.domain.model.user.Visitor;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.ecnusmartboys.infrastructure.config.CosConfig;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.infrastructure.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserInfoConvertor userInfoConvertor;
    private final VisitorInfoConvertor visitorInfoConvertor;
    private final SupervisorInfoConvertor supervisorInfoConvertor;
    private final ConsultantInfoConvertor consultantInfoConvertor;
    private final UpdateVisitorReqConvertor updateVisitorReqConvertor;
    private final OnlineStateService onlineStateService;
    private final ImClient adminClient;

    @Resource
    CosConfig cosConfig;

    private static final String BASE_URL = "https://freud-1311238733.cos.ap-shanghai.myqcloud.com/";

    @Override
    public Responses<UserInfo> getUserInfo(Common common) {
        var user = userRepository.retrieveById(common.getUserId());
        if (user == null) {
            return Responses.ok(null);
        }
        UserInfo userInfo;
        if (user instanceof Visitor) {
            userInfo = visitorInfoConvertor.fromEntity((Visitor) user);
        } else if (user instanceof Supervisor) {
            userInfo = supervisorInfoConvertor.fromEntity((Supervisor) user);
        } else if (user instanceof Consultant) {
            userInfo = consultantInfoConvertor.fromEntity((Consultant) user);
        } else {
            userInfo = userInfoConvertor.fromEntity(user);
        }
        return Responses.ok(userInfo);
    }

    @Override
    @Transactional
    public Responses<Object> updateVisitorInfo(UpdateVisitorRequest req, Common common) {
        var visitor = updateVisitorReqConvertor.toEntity(req);
        visitor.setId(common.getUserId());
        userRepository.update(visitor);
        return Responses.ok();
    }

    @Override
    public void offline(Long userId) {
        onlineStateService.setUserState(userId, OnlineState.OFFLINE);
        var kickRequest = KickRequest.builder().userId(userId.toString()).build();
        try {
            adminClient.account.kick(kickRequest);
        } catch (IOException e) {
            log.error("IM踢下线失败, userId {}, {}", userId, e.getMessage());
        }
    }

    @Override
    public Responses<String> saveAvatar(MultipartFile file, Common common) {
        // 获取输入流
        try {
            InputStream inputStream = file.getInputStream();
            // 创建 PutObjectRequest 对象，并指定输入流和 COS 存储路径
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            String newPath = "avatar/" + System.currentTimeMillis() + "/" + file.getOriginalFilename();
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.cosBucket(),
                    newPath, inputStream, metadata);
            // 执行文件上传
            PutObjectResult putObjectResult = cosConfig.cosClient().putObject(putObjectRequest);
            // 关闭 InputStream
            inputStream.close();

            // 处理上传结果
            if (putObjectResult == null) {
                // 上传失败
                throw new BadRequestException("上传文件失败");
            }

            return Responses.ok(BASE_URL + newPath);
        } catch (IOException e) {
            throw new BusinessException(402, "文件转为字节流失败");
        }
    }

    @Override
    public Responses<String> updatePsdAndAvatar(UpdatePsdAndAvatarRequest req, Common common) {
        var user = userRepository.retrieveById(common.getUserId());

        if(!Objects.equals(user.getPassword(), req.getOldPsd())) {
            throw new BadRequestException("旧密码不正确");
        }

        user.setAvatar(req.getAvatar());
        user.setPassword(req.getNewPsd());

        userRepository.update(user);
        return Responses.ok("修改成功");
    }
}
