package org.ecnusmartboys.application.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndConsultationNotification {

    private String consultationId;

    private String helpId;

    private String VisitorName;

    private String ConsultantName;

    private String supervisorName;
}
