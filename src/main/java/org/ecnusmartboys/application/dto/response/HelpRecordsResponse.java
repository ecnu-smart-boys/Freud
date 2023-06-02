package org.ecnusmartboys.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.application.dto.HelpRecordInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpRecordsResponse {

    private List<HelpRecordInfo> records;

    private long total;
}
