package org.ecnusmartboys.domain.model.online;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OnlineVisitor {

    public final static long NULL_CONSULTANT = -1L;

    private Long consultant;

    private Long wait;

    public OnlineVisitor() {
        consultant = NULL_CONSULTANT;
        wait = NULL_CONSULTANT;
    }

    public void startConsultation(long consultant) {
        this.consultant = consultant;
        this.wait = NULL_CONSULTANT;
    }

    public void endConsultation() {
        this.consultant = NULL_CONSULTANT;
    }

    public boolean startWaiting(long consultant) {
        if(isBusy()) {
            return false;
        }

        this.wait = consultant;
        return true;
    }

    public long endWaiting() {
        var temp = this.wait;
        this.wait = NULL_CONSULTANT;
        return temp;
    }

    public boolean isBusy() {
        return consultant != NULL_CONSULTANT || wait != NULL_CONSULTANT;
    }
}
