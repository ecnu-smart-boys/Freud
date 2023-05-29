package user;

import org.ecnusmartboys.FreudApp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = FreudApp.class)
@ActiveProfiles({"local", "test"})
public class ArrangementDOTest {


    @Test
    public void addArrangementTest() {
//        AddArrangementRequest req =  new AddArrangementRequest();
//        req.setDate(new Date());
//        req.setUserId(11L);
//        if(userService.getSingleUser(req.getUserId(), UserService.ROLE_SUPERVISOR) == null) {
//            throw new BadRequestException("所要排班的督导不存在");
//        }
//
//        if(arrangementService.getArrangement(req) != null) {
//            throw new BadRequestException("请勿重复排班");
//        }
//
//        Arrangement arrangement = new Arrangement(req.getDate(), req.getUserId());
//        arrangementService.save(arrangement);
    }
}
