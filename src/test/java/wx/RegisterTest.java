package wx;

import org.ecnusmartboys.FreudApp;
import org.ecnusmartboys.adaptor.controller.AuthController;
import org.ecnusmartboys.infrastructure.mapper.VisitorInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@SpringBootTest(classes = FreudApp.class)
@ActiveProfiles({"local", "test"})
@Transactional
public class RegisterTest {

    @Resource
    AuthController authController;

    @Resource
    VisitorInfoMapper visitorInfoMapper;

    @Test
    public void testRegister() {
//        var mock = new MockHttpServletRequest();
//        var req = new WxRegisterRequest();
//        req.setAge(18);
//        req.setAvatar("https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJibicQicqicibi");
//        req.setGender(1);
//        req.setName("test");
//        req.setPhone("13666666667");
//        req.setEmergencyContact("TEST");
//        req.setEmergencyPhone("13666666666");
//
//        var u = authController.register(req, mock).getData();
//        assertNotNull(u);
//        assertNotNull(u.getId());
//
//        var user = userService.getById(u.getId());
//        assertNotNull(user);
//        assertEquals(user.getAge(), req.getAge());
//        assertEquals(user.getGender(), req.getGender());
//        assertEquals(user.getName(), req.getName());
//        assertEquals(user.getAvatar(), req.getAvatar());
//        assertEquals(user.getDisabled(), false);
//        user.getRoles().forEach(role -> assertEquals("visitor", role));
//
//        var visitor = visitorInfoMapper.selectById(u.getId());
//        assertNotNull(visitor);
//        assertEquals(visitor.getEmergencyContact(), req.getEmergencyContact());
//        assertEquals(visitor.getEmergencyPhone(), req.getEmergencyPhone());
    }
}
