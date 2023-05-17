package user;

import org.ecnusmartboys.FreudApp;
import org.ecnusmartboys.application.dto.request.command.AddArrangementReq;
import org.ecnusmartboys.infrastructure.service.UserService;
import org.ecnusmartboys.infrastructure.exception.BadRequestException;
import org.ecnusmartboys.infrastructure.model.mysql.Arrangement;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest(classes = FreudApp.class)
@ActiveProfiles({"local", "test"})
public class ArrangementTest {

    @Resource
    ArrangementService arrangementService;

    @Resource
    UserService userService;

    @Test
    public void addArrangementTest() {
        AddArrangementReq req =  new AddArrangementReq();
        req.setDate(new Date());
        req.setUserId(11L);
        if(userService.getSingleUser(req.getUserId(), UserService.ROLE_SUPERVISOR) == null) {
            throw new BadRequestException("所要排班的督导不存在");
        }

        if(arrangementService.getArrangement(req) != null) {
            throw new BadRequestException("请勿重复排班");
        }

        Arrangement arrangement = new Arrangement(req.getDate(), req.getUserId());
        arrangementService.save(arrangement);
    }
}
