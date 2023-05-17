package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorsDTO {

    private List<SupervisorInfo> supervisors;

    private Long total;
}
