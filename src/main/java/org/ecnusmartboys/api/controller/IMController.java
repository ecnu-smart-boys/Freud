package org.ecnusmartboys.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.doocs.im.model.callback.AfterMsgWithdrawCallback;
import io.github.doocs.im.model.callback.AfterSendMsgCallback;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecnusmartboys.api.annotation.AnonymousAccess;
import org.ecnusmartboys.application.dto.request.command.QueryMessageRequest;
import org.ecnusmartboys.application.dto.response.Responses;
import org.ecnusmartboys.domain.model.PageResult;
import org.ecnusmartboys.infrastructure.config.IMConfig;
import org.ecnusmartboys.infrastructure.data.im.IMCallbackParam;
import org.ecnusmartboys.infrastructure.data.mysql.table.MessageDO;
import org.ecnusmartboys.infrastructure.mapper.MessageMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.ecnusmartboys.infrastructure.data.im.CallbackCommand.AFTER_MSG_WITHDRAW;
import static org.ecnusmartboys.infrastructure.data.im.CallbackCommand.AFTER_SEND_MSG;

@Slf4j
@RestController
@RequestMapping("/im")
@RequiredArgsConstructor
@Api(tags = "IM管理接口")
public class IMController {

    private final IMConfig imConfig;

    private final MessageMapper messageMapper;

    private final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @PostMapping("/cb")
    @AnonymousAccess
    public Responses<?> callback(IMCallbackParam param,
        @RequestBody String body, HttpServletRequest request) {
        try {
            switch (param.getCallbackCommand()) {
                case AFTER_SEND_MSG: {
                    var cb = mapper.readValue(body, AfterSendMsgCallback.class);
                    var key = cb.getMsgKey(); // 消息的唯一表示
                    var msgBody = mapper.writeValueAsString(cb.getMsgBody());

                    var message = new MessageDO();
                    message.setMsgKey(key);
                    message.setFromId(cb.getFromAccount());
                    message.setToId(cb.getToAccount());
                    message.setMsgBody(msgBody);
                    message.setTime(cb.getMsgTime());
                    message.setRevoked(false);
                    messageMapper.insert(message);
                    log.debug("im callback msg: {}", message);
                    break;
                }
                case AFTER_MSG_WITHDRAW: {
                    var cb = mapper.readValue(body, AfterMsgWithdrawCallback.class);
                    var key = cb.getMsgKey(); // 消息的唯一表示

                    var wrapper = new QueryWrapper<MessageDO>();
                    wrapper.eq("msg_key", key);
                    var update = new MessageDO();
                    update.setRevoked(true);
                    messageMapper.update(update, wrapper);

                    break;
                }
            }
        } catch (JsonProcessingException | NumberFormatException e) {
            log.error("Parse im callback error: ", e);
        }
        return Responses.ok();
    }

    @ApiOperation(value = "获取消息", notes = "确保消息按时间顺序倒序建议加上?order=time,desc")
    @GetMapping("/list-message")
    public Responses<PageResult<MessageDO>> listMessage(@Validated QueryMessageRequest request){
        var result = messageMapper.selectPage(request.toPage(), request.toQueryWrapper());
        return Responses.ok(new PageResult<>(result.getRecords(), result.getTotal()));
    }
}
