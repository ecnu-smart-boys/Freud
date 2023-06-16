package org.ecnusmartboys.domain.model.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.domain.model.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Help {

    private String helpId;
    private Long startTime;
    private Long endTime;
    private User supervisor;
}
