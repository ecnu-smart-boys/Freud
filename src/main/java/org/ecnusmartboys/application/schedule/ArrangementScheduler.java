package org.ecnusmartboys.application.schedule;

import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.domain.repository.ArrangementRepository;
import org.ecnusmartboys.domain.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ArrangementScheduler {

    private final ArrangementRepository arrangementRepository;

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 ? * ?")
    public void setTodaysArrangement() {
        var now = new Date();
        var dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == -1) {
            dayOfWeek = 6;
        }

        var staffIds = userRepository.retrieveIdsByArrangement(dayOfWeek);

        for (var id : staffIds) {
            try {
                // 考虑到之前已经单独排班可能出现重复排班（主键重复）的情况
                arrangementRepository.save(id, now);
            } catch (DuplicateKeyException ignore) {

            }
        }
    }
}
