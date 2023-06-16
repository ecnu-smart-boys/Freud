package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.VisitorInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorsResponse {
    private List<VisitorInfo> visitorInfos;

    private Long total;
}
