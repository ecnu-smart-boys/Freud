package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ecnusmartboys.application.dto.RankUserInfo;

import java.util.List;

@AllArgsConstructor
@Data
public class RankResponse {

    private List<RankUserInfo> consultations;

    private List<RankUserInfo> goodComments;
}
