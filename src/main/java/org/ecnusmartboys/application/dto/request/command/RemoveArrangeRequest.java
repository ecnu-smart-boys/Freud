package org.ecnusmartboys.application.dto.request.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecnusmartboys.api.annotation.Timestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
public class RemoveArrangeRequest {

    @NotNull
    private String userId;

    @NotNull
    @Timestamp
    private Long timestamp;
}
