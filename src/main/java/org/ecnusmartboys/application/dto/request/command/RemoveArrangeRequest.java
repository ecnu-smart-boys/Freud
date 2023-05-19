package org.ecnusmartboys.application.dto.request.command;

import lombok.Data;

import java.util.Date;

@Data
public class RemoveArrangeRequest {
    private String userId;
    private Date date;
}
