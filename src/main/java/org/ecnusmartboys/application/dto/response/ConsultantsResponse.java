package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.ConsultantInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantsResponse {

    private List<ConsultantInfo> consultants;

    private Long total;
}
