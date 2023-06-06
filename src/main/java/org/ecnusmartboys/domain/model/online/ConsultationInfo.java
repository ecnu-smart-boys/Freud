package org.ecnusmartboys.domain.model.online;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationInfo {

    private String visitorId;

    private String consultantId;
}
