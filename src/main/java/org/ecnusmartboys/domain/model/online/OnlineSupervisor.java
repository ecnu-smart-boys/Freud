package org.ecnusmartboys.domain.model.online;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@Data
public class OnlineSupervisor {

    Set<Long> consultants;

    Integer maxConcurrent;

    public OnlineSupervisor() {
        consultants = new HashSet<>();
    }
}
