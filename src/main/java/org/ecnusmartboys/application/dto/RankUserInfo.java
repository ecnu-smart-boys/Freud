package org.ecnusmartboys.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankUserInfo {

    private String avatar;

    private String name;

    private int total;
}
