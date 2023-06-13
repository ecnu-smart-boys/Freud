package org.ecnusmartboys.application.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndHelpNotification {

    private String consultationId;

    private String helpId;

    private String consultantName;

    private String supervisorName;
}
