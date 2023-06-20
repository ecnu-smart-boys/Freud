package mysql;

import org.ecnusmartboys.FreudApp;
import org.ecnusmartboys.domain.repository.ConversationRepository;
import org.ecnusmartboys.infrastructure.mapper.ConversationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest(classes = FreudApp.class)
@ActiveProfiles({"local", "test"})
@Transactional
public class MysqlTest {

    @Resource
    ConversationMapper mapper;

    @Resource
    ConversationRepository repository;

    @Test
    public void test() {
//        var list = mapper.selectConsultationsByToId("", "1970-01-01", "34");
//        list.forEach(elem -> {
//            System.out.println(elem.toString());
//        });
        repository.retrieveConsultationsByToUser(0L, 5L, "", 0L, "34");
    }
}
