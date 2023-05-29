package org.ecnusmartboys.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Consulvisor {

    private String consultantId;

    private String supervisorId;
}
