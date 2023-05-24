package org.ecnusmartboys.application.dto.request.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
public class RemoveArrangeRequest {

    private String userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
