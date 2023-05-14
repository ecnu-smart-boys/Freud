package im;


import io.github.doocs.im.ImClient;
import io.github.doocs.im.constant.MsgType;
import io.github.doocs.im.constant.SyncOtherMachine;
import io.github.doocs.im.model.message.*;
import io.github.doocs.im.model.request.AdminGetRoamMsgRequest;
import io.github.doocs.im.model.request.AdminMsgWithdrawRequest;
import io.github.doocs.im.model.request.SendMsgRequest;
import io.github.doocs.im.model.response.AdminRoamMsgResult;
import io.github.doocs.im.model.response.MsgListItem;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.FreudApp;
import org.ecnusmartboys.infrastructure.config.IMConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Slf4j
@SpringBootTest(classes = FreudApp.class)
@ActiveProfiles({"local", "test"})
@Transactional
public class IMCallbackTest {

    @Resource
    IMConfig imConfig;


    ImClient client;

    @BeforeEach
    public void init() {
        long appId = imConfig.getAppId();
        String key = imConfig.getSecretKey();
        client = ImClient.getInstance(appId, "administrator", key);
    }

    @Test
    public void testSendMessage() throws IOException {
        TIMTextMsgElement msg = new TIMTextMsgElement("hello world");
        TIMImageMsgElement imageMsg = new TIMImageMsgElement(
                new TIMImageMsgElement.ImageMsgContent(
                        "abcdef600800", 1,
                        Arrays.asList(new TIMImageMsgElement.ImageInfo(1, 600 * 800 * 3, 600, 800, "https://example.com/1.jpg"))));
        TIMSoundMsgElement soundMsg = new TIMSoundMsgElement(
                new TIMSoundMsgElement.SoundMsgContent(
                        "https://", "uuid88000d", 1000, 2, 1));
        List<TIMMsgElement> msgBody = new ArrayList<>();
        msgBody.add(msg);
        msgBody.add(imageMsg);
        msgBody.add(soundMsg);

        SendMsgRequest request = SendMsgRequest.builder()
                .fromAccount("1222_1")
                .toAccount("1_1")
                .msgRandom(123L)
                .msgBody(msgBody)
                .syncOtherMachine(SyncOtherMachine.YES)
                .msgTimeStamp(1631934058)
                .msgLifeTime(604800)
                .build();
        var result = client.message.sendMsg(request);
        log.info(result.toString());
    }

    @Test
    public void testRetrieveMessage() throws IOException {
        AdminGetRoamMsgRequest request = AdminGetRoamMsgRequest.builder()
                .fromAccount("1222_1")
                .toAccount("1_1")
                .maxCnt(123)
                .minTime(1600000000)
                .maxTime(1701934060)
                .build();

        AdminRoamMsgResult result = client.message.getRoamMsg(request);
        List<MsgListItem> msgList = result.getMsgList();
        if (msgList != null && msgList.size() > 0) {
            for (MsgListItem item : msgList) {
                List<TIMMsgElement> msgBody = item.getMsgBody();
                if (msgBody != null && msgList.size() > 0) {
                    for (TIMMsgElement msgElement : msgBody) {
                        // 根据 msgType 强转为对应的子类
                        if (Objects.equals(msgElement.getMsgType(), MsgType.TIM_CUSTOM_ELEM)) {
                            TIMCustomMsgElement t = (TIMCustomMsgElement) msgElement;
                            System.out.println(t.getMsgContent().getDesc());
                        } else if (Objects.equals(msgElement.getMsgType(), MsgType.TIM_TEXT_ELEM)) {
                            TIMTextMsgElement t = (TIMTextMsgElement) msgElement;
                            System.out.println(t.getMsgContent().getText());
                        }
                    }
                }
            }
        }
    }

    @Test
    public void tesrWithdrawMessage() throws IOException {
        var req = new AdminMsgWithdrawRequest("1222_1", "1_1", "708427144_123_1683290206");
        var result = client.message.msgWithdraw(req);
        log.info(result.toString());
    }
}
