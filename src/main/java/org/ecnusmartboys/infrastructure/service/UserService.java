package org.ecnusmartboys.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ecnusmartboys.application.dto.UserInfo;
import org.ecnusmartboys.application.dto.request.command.*;
import org.ecnusmartboys.application.dto.request.query.UserListReq;
import org.ecnusmartboys.infrastructure.data.mysql.UserDO;

import java.util.List;

public interface UserService extends IService<UserDO> {

    String ROLE_VISITOR = "visitor";
    String ROLE_CONSULTANT = "consultant";
    String ROLE_SUPERVISOR = "supervisor";
    String ROLE_ADMIN = "admin";

    /**
     * 获取用户信息
     * @param id 用户id
     * @return 用户信息
     */
    UserInfo getUserInfo(Long id);

    /**
     * 获得用户列表
     * @param req 查询参数
     * @param role 角色
     * @return 用户实体类列表
     */
    List<UserDO> getUsers(UserListReq req, String role);

    /**
     * 获得某种用户总数
     * @return 返回该用户数量
     */
    long getUserCount(String role);

    /**
     * 禁用用户
     * @param userId 用户id
     * @param role 用户角色
     */
    void disable(Long userId, String role);

    /**
     * 启用用户
     * @param userId 用户id
     * @param role 用户角色
     */
    void enable(Long userId, String role);

    /**
     * 添加督导
     * @param req 添加督导参数
     */
    void saveSupervisor(AddSupervisorRequest req);

    /**
     * 更新督导
     * @param req 更新督导参数
     */
    void updateSupervisor(UpdateSupervisorRequest req);

    /**
     * 添加咨询师
     * @param req 添加咨询师参数
     */
    void saveConsultant(AddConsultantRequest req);

    /**
     * 通过用户名查询用户
     * @param username 用户名
     * @return 用户
     */
    UserDO getByUsername(String username);

    /**
     * 通过手机号查询用户
     * @param phone 手机号
     * @return 用户
     */
    UserDO getByPhone(String phone);

    /**
     * 获得单个用户
     * @param userId 用户id
     * @param role 用户角色
     * @return 用户
     */
    UserDO getSingleUser(Long userId, String role);



}
