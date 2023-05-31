package org.ecnusmartboys.application.schedule;

import io.github.doocs.im.ImClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.application.service.OnlineStateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeoutKickScheduler {

    private final OnlineStateService onlineStateService;
    private final ImClient adminClient;

    @Scheduled(cron = "15 * * * * ?")
    public void timeoutKick(){
        var timeoutUsers = onlineStateService.timeoutKick();
        for (Long userId : timeoutUsers) {
            var kickRequest = io.github.doocs.im.model.request.KickRequest.builder().userId(userId.toString()).build();
            try {
                adminClient.account.kick(kickRequest);
            } catch (IOException e) {
                log.error("IM踢下线失败, userId {}, {}", userId, e.getMessage());
            }
        }
    }

}
