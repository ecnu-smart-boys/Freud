package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DayConsultInfo {

    private Long timestamp;

    private Integer consultationCount;
}
