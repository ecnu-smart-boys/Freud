package org.ecnusmartboys.application.schedule;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.application.service.OnlineStateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeoutKickScheduler {

    private final OnlineStateService onlineStateService;

    @Scheduled(cron = "15 * * * * ?")
    public void timeoutKick(){
        onlineStateService.timeoutKick();
    }

}
