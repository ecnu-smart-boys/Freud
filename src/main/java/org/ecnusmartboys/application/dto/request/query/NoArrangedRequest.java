package org.ecnusmartboys.application.dto.request.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecnusmartboys.adaptor.annotation.Timestamp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoArrangedRequest {

    @Pattern(regexp = "^[\\p{L}a-zA-Z]{0,32}$", message = "姓名格式不正确")
    private String name = "";

    @NotNull(message = "时间戳不能为空")
    @Timestamp
    private Long timestamp;
}
