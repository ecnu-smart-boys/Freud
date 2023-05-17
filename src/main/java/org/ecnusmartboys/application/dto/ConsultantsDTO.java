package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantsDTO {

    private List<ConsultantInfo> consultants;

    private Long total;
}
