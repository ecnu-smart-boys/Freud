package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.ecnusmartboys.api.annotation.Timestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel("给咨询师或督导排班请求")
public class AddArrangementRequest {

    @NotNull(message = "用户id不能为空")
    private String userId;

    @NotNull
    @Timestamp
    private Long timestamp;
}
