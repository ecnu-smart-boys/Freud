package org.ecnusmartboys.application.dto.request.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisableUserRequest {

    @NotNull
    String id;
}
