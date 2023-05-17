package org.ecnusmartboys.application.dto.request.command;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel("给咨询师或督导排班请求")
public class AddArrangementReq {

    private Long userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
