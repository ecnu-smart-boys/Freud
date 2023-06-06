package org.ecnusmartboys.domain.model.online;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Data
public class OnlineConsultant {
    Set<Long> supervisors;

    Set<Long> visitors;

    List<ConsultationInfo> waitingList;

    Integer maxConcurrent;

    Long userId;

    public OnlineConsultant() {
        supervisors = new HashSet<>();
        visitors = new HashSet<>();
        waitingList = new ArrayList<>();
    }
}

