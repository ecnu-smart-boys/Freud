package org.ecnusmartboys.application.dto.request.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoArrangedRequest {

    @Size(max = 32, message = "名字长度不能超过32位")
    private String name = "";

    @NotNull(message = "时间戳不能为空")
    @Min(value = 0, message = "时间戳不能为负数")
    private Long timestamp;
}
