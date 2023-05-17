package org.ecnusmartboys.domain.schedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.ecnusmartboys.domain.service.ArrangementService;
import org.ecnusmartboys.domain.service.StaffService;
import org.ecnusmartboys.domain.service.UserService;
import org.ecnusmartboys.infrastructure.data.mysql.Arrangement;
import org.ecnusmartboys.infrastructure.data.mysql.Staff;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ArrangementScheduler {

    private final ArrangementService arrangementService;

    private final StaffService staffService;

    @Scheduled(cron = "0 0 0 ? * ?")
    public void setTodaysArrangement(){
        var now = new Date();
        var dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == -1){
            dayOfWeek = 6;
        }
        var wrapper = new QueryWrapper<Staff>().eq(
            "arrangement & " + (1 << dayOfWeek), 1 << dayOfWeek);
        var staffList = staffService.list(wrapper);

        for (Staff staff : staffList) {
            var a = new Arrangement();
            a.setDate(now);
            a.setUserId(staff.getId());
            try {
                // 考虑到之前已经单独排班可能出现重复排班（主键重复）的情况
                arrangementService.save(a);
            } catch (DuplicateKeyException ignore) {

            }
        }
    }
}
