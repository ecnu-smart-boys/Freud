package org.ecnusmartboys.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.dto.enums.OnlineState;
import org.ecnusmartboys.application.service.OnlineStateService;
import org.ecnusmartboys.domain.repository.OnlineUserRepository;
import org.ecnusmartboys.infrastructure.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("onlineStateService")
@RequiredArgsConstructor
public class OnlineStateServiceImpl implements OnlineStateService {

    private static final String KEY_PREFIX = "onlineState:";
    private static final String TIMEOUT_KEY = "onlineStateTimeout";
    private final RedisUtil redisUtil;
    private final OnlineUserRepository onlineUserRepository;
    @Value("${freud.online.timeout:15}")
    private int timeoutInMinutes;

    @Override
    public OnlineState getUserState(Long userId) {
        String key = getKey(userId);
        Integer ordinal = (Integer) redisUtil.get(key);
        return ordinal == null ? OnlineState.OFFLINE : OnlineState.values()[ordinal];
    }

    @Override
    public void setUserState(Long userId, OnlineState state) {
        String key = getKey(userId);
        switch (state) {
            case OFFLINE -> {
                // 从redis中移除用户
                redisUtil.del(key);
                redisUtil.zRemove(TIMEOUT_KEY, userId);
            }
            default -> {
                // 超时更新
                long score = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(timeoutInMinutes);
                redisUtil.zAdd(TIMEOUT_KEY, userId, score);
                // 设置状态
                redisUtil.set(key, state.ordinal());
            }
        }
    }

    @Override
    public void refreshTimeout(Long userId, long timeout, TimeUnit unit) {
        long score = System.currentTimeMillis() + unit.toMillis(timeout);
        redisUtil.zAdd(TIMEOUT_KEY, userId, score);
    }

    @Override
    public List<Long> timeoutKick() {
        // Warning: not atomic
        long max = System.currentTimeMillis();
        var timeoutUserIdSet = redisUtil.zRangeByScore(TIMEOUT_KEY, 0, max);
        List<Long> ids = new ArrayList<>(timeoutUserIdSet.size());
        List<String> keys = new ArrayList<>(timeoutUserIdSet.size());
        for (Object o : timeoutUserIdSet) {
            ids.add(Long.parseLong(o.toString()));
            keys.add(KEY_PREFIX + o);
            onlineUserRepository.logout(o.toString());
        }
        redisUtil.zRemoveRangeByScore(TIMEOUT_KEY, 0, max);
        redisUtil.del(keys);

        return ids;
    }

    private String getKey(Long userId) {
        return KEY_PREFIX + userId;
    }
}
