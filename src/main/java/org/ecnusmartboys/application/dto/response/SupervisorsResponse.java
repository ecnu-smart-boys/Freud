package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.SupervisorInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorsResponse {

    private List<SupervisorInfo> supervisors;

    private Long total;
}
