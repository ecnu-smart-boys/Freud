package org.ecnusmartboys.application.dto.request.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class RemoveArrangeRequest {
    private String userId;
    private Date date;
}
