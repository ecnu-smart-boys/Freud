package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorsDTO {

    private List<VisitorInfo> visitors;

    private Long total;
}
