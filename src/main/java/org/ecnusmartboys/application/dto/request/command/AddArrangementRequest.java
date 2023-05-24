package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel("给咨询师或督导排班请求")
public class AddArrangementRequest {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "排班日期不能为空")
    private Date date;

    private String role;
}
