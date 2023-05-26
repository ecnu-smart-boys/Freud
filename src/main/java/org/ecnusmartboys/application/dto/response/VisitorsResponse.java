package org.ecnusmartboys.application.dto.response;

import lombok.Data;
import org.ecnusmartboys.application.dto.UserInfo;

import java.util.List;

@Data
public class VisitorsResponse {
    private List<UserInfo> supervisors;

    private Long total;
}
