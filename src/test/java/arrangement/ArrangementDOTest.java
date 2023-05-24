package arrangement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.FreudApp;
import org.ecnusmartboys.domain.schedule.ArrangementScheduler;
import org.ecnusmartboys.domain.service.ArrangementService;
import org.ecnusmartboys.domain.service.StaffService;
import org.ecnusmartboys.infrastructure.data.mysql.ArrangementDO;
import org.ecnusmartboys.infrastructure.data.mysql.Staff;
import org.ecnusmartboys.infrastructure.repository.ArrangementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(classes = FreudApp.class)
@ActiveProfiles({"local", "test"})
@Transactional
public class ArrangementDOTest {

    @Resource
    ArrangementScheduler scheduler;

    @Resource
    ArrangementService arrangementService;

    @Resource
    ArrangementRepository arrangementRepository;

    @Resource
    StaffService staffService;

    @Test
    public void testSetTodaysArrangement() {
        scheduler.setTodaysArrangement();

        var now = new Date();
        var dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == -1){
            dayOfWeek = 6;
        }
        var wrapper = new QueryWrapper<Staff>().eq(
            "arrangement & " + (1 << dayOfWeek), 1 << dayOfWeek);
        var staffList = staffService.list(wrapper);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Staff staff : staffList) {
            var w = new QueryWrapper<ArrangementDO>()
                .eq("date", now)
                .eq("user_id", staff.getId());
            var arr = arrangementRepository.selectOneArrangement(staff.getId(), dateFormat.format(now));
            assertNotNull(arr);
        }
    }
}
