package org.ecnusmartboys.application.dto.request.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecnusmartboys.adaptor.annotation.Timestamp;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class RemoveArrangeRequest {

    @NotNull
    private String userId;

    @NotNull
    @Timestamp
    private Long timestamp;
}
