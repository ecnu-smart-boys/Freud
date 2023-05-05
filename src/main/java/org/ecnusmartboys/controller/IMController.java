package org.ecnusmartboys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.doocs.im.model.callback.AfterMsgWithdrawCallback;
import io.github.doocs.im.model.callback.AfterSendMsgCallback;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.annotation.AnonymousAccess;
import org.ecnusmartboys.config.IMConfig;
import org.ecnusmartboys.model.entity.Message;
import org.ecnusmartboys.model.im.IMCallbackParam;
import org.ecnusmartboys.model.response.BaseResponse;
import org.ecnusmartboys.repository.MessageRepository;
import org.ecnusmartboys.utils.SecurityUtil;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import static org.ecnusmartboys.model.im.CallbackCommand.AFTER_MSG_WITHDRAW;
import static org.ecnusmartboys.model.im.CallbackCommand.AFTER_SEND_MSG;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/im")
@Api(tags = "IM即时通信")
public class IMController {

    private final IMConfig imConfig;

    private final MessageRepository messageRepository;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @GetMapping("/userSig")
    public BaseResponse<String> userSig() {
        var userId = SecurityUtil.getCurrentUserId();
        var userSig = imConfig.getUserSig(String.valueOf(userId));
        return BaseResponse.ok("ok", userSig);
    }

    @PostMapping("/cb")
    @AnonymousAccess
    public BaseResponse<?> callback(IMCallbackParam param, @RequestBody String body, HttpServletRequest request) {
        try {
            switch (param.getCallbackCommand()) {
            case AFTER_SEND_MSG: {
                var cb = mapper.readValue(body, AfterSendMsgCallback.class);
                var toAccount = userAndConversationIdFromAccount(cb.getToAccount()); // 消息的接收者
                var fromAccount = userAndConversationIdFromAccount(cb.getFromAccount()); // 消息的发送者
                Assert.isTrue(fromAccount.getSecond().equals(toAccount.getSecond()), "会话ID不一致");
                var key = cb.getMsgKey(); // 消息的唯一表示
                var msgBody = mapper.writeValueAsString(cb.getMsgBody());

                var message = new Message();
                message.setMsgKey(key);
                message.setConversationId(toAccount.getSecond());
                message.setFromId(fromAccount.getFirst());
                message.setToId(toAccount.getFirst());
                message.setMsgBody(msgBody);
                message.setTime(new Date(cb.getMsgTime() * (long)1000));
                messageRepository.insert(message);
                log.debug("im callback msg: {}", message);
                break;
            }
            case AFTER_MSG_WITHDRAW: {
                var cb = mapper.readValue(body, AfterMsgWithdrawCallback.class);
                var key = cb.getMsgKey(); // 消息的唯一表示
                var wrapper = new QueryWrapper<Message>();
                wrapper.eq("msg_key", key);
                messageRepository.delete(wrapper);
                break;
            }
            }
        } catch (JsonProcessingException | NumberFormatException e) {
            log.error("Parse im callback error: ", e);
        }
        return null;
    }

    /**
     * 从account中解析出用户id和会话id
     * @param account account格式为: userId_conversationId
     * @return 用户id和会话id
     */
    private Pair<Long, Long> userAndConversationIdFromAccount(String account){
        String[] split = account.split("_");
        return Pair.of(Long.parseLong(split[0]), Long.parseLong(split[1]));
    }

}
