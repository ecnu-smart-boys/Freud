package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.ConsultRecordInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultRecordsResponse {

    private List<ConsultRecordInfo> records;

    private long total;
}
