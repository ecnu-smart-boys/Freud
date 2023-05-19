package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.UserInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantsResponse {

    private List<UserInfo> consultants;

    private Long total;
}
