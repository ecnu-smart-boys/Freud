package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.enums.OnlineState;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 在线状态服务，开始会话时需要对在线状态进行判断
 */
public interface OnlineStateService {

    /**
     * 获取用户在线状态
     *
     * @param userId
     * @return
     */
    OnlineState getUserState(Long userId);

    /**
     * 设置用户在线状态
     *
     * @param userId
     * @param state
     */
    void setUserState(Long userId, OnlineState state);

    /**
     * 用户长时间无心跳会踢下线，刷新用户踢下线时间
     *
     * @param userId
     * @param timeout
     * @param unit
     */
    void refreshTimeout(Long userId, long timeout, TimeUnit unit);

    /**
     * 将所有超时无心跳用户设置为离线，并返回对应的用户ID列表
     *
     * @return 被踢下线的用户id
     */
    List<Long> timeoutKick();
}
